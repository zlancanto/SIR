package jpa.services.impl;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jpa.dao.abstracts.ConcertDao;
import jpa.dao.abstracts.CustomerDao;
import jpa.dao.abstracts.TicketDao;
import jpa.dto.ticket.PurchaseTicketsRequestDto;
import jpa.dto.ticket.ResponseCustomerTicketDto;
import jpa.dto.ticket.ResponseTicketDetailsDto;
import jpa.entities.Concert;
import jpa.entities.Customer;
import jpa.entities.Ticket;
import jpa.enums.ConcertStatus;
import jpa.services.interfaces.TicketService;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

import static jpa.utils.StringValidation.normalizeRequired;

/**
 * Service implementation TicketServiceImpl.
 */
public class TicketServiceImpl implements TicketService {
    private final TicketDao ticketDao;
    private final ConcertDao concertDao;
    private final CustomerDao customerDao;

    /**
     * Creates a new instance of TicketServiceImpl.
     *
     * @param ticketDao method parameter
     */
    public TicketServiceImpl(
            TicketDao ticketDao,
            ConcertDao concertDao,
            CustomerDao customerDao
    ) {
        this.ticketDao = ticketDao;
        this.concertDao = concertDao;
        this.customerDao = customerDao;
    }

    /**
     * Executes purchaseTickets operation.
     *
     * @param request method parameter
     * @param authenticatedCustomerEmail method parameter
     * @return operation result
     */
    @Override
    public List<ResponseTicketDetailsDto> purchaseTickets(
            PurchaseTicketsRequestDto request,
            String authenticatedCustomerEmail
    ) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }
        if (request.concertId() == null) {
            throw new BadRequestException("concertId is required");
        }
        if (request.quantity() == null) {
            throw new BadRequestException("quantity is required");
        }
        if (request.quantity() <= 0) {
            throw new BadRequestException("quantity must be greater than 0");
        }

        String customerEmail = normalizeRequired("authenticatedCustomerEmail", authenticatedCustomerEmail)
                .toLowerCase(Locale.ROOT);

        Customer customer = customerDao.findByEmail(customerEmail)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        Concert concert = concertDao.findOne(request.concertId());
        if (concert == null) {
            throw new NotFoundException("Concert not found");
        }

        if (concert.getStatus() != ConcertStatus.PUBLISHED) {
            throw new ClientErrorException("Concert is not published", Response.Status.CONFLICT);
        }

        if (concert.getDate() == null || !concert.getDate().isAfter(Instant.now())) {
            throw new ClientErrorException("Concert already started", Response.Status.CONFLICT);
        }

        List<Ticket> reserved = ticketDao.reserveAvailableTickets(concert.getId(), customer, request.quantity());
        if (reserved.size() < request.quantity()) {
            throw new ClientErrorException("Not enough tickets available", Response.Status.CONFLICT);
        }

        return reserved.stream()
                .map(this::toResponse)
                .toList();
    }

    private ResponseTicketDetailsDto toResponse(Ticket ticket) {
        return new ResponseTicketDetailsDto(
                ticket.getId(),
                ticket.getBarcode(),
                ticket.getPrice()
        );
    }

    /**
     * Executes getCustomerTickets operation.
     *
     * @param authenticatedCustomerEmail method parameter
     * @return operation result
     */
    @Override
    public List<ResponseCustomerTicketDto> getCustomerTickets(String authenticatedCustomerEmail) {
        String customerEmail = normalizeRequired("authenticatedCustomerEmail", authenticatedCustomerEmail)
                .toLowerCase(Locale.ROOT);

        Customer customer = customerDao.findByEmail(customerEmail)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        return ticketDao.findCustomerTicketsProjection(customer.getId());
    }
}
