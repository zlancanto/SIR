package jpa.services.impl;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jpa.dao.abstracts.AdminDao;
import jpa.dao.abstracts.ConcertDao;
import jpa.dao.abstracts.OrganizerDao;
import jpa.dao.abstracts.PlaceDao;
import jpa.dao.abstracts.UserDao;
import jpa.dto.concert.CreateConcertRequestDto;
import jpa.dto.concert.ResponseConcertDetailsDto;
import jpa.dto.concert.ResponseConcertPlaceDto;
import jpa.dto.concert.ResponseOrganizerConcertDto;
import jpa.entities.Admin;
import jpa.entities.Concert;
import jpa.entities.Organizer;
import jpa.entities.Place;
import jpa.entities.Ticket;
import jpa.entities.User;
import jpa.enums.ConcertStatus;
import jpa.services.interfaces.ConcertService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static jpa.utils.StringValidation.normalizeRequired;

/**
 * Default implementation of concert lifecycle use cases.
 *
 * <p>This service validates input payloads, resolves linked entities
 * (organizer, place, admin), enforces workflow transitions and maps
 * persisted entities to API DTOs.</p>
 */
public class ConcertServiceImpl implements ConcertService {
    /* Until explicit start/end are modeled,
    * each concert reserves a venue for a fixed 3-hour slot. */
    private static final Duration PLACE_BOOKING_DURATION = Duration.ofHours(3);
    private static final List<ConcertStatus> PLACE_BOOKING_BLOCKING_STATUSES = List.of(
            ConcertStatus.PENDING_VALIDATION,
            ConcertStatus.PUBLISHED
    );

    private final ConcertDao concertDao;
    private final OrganizerDao organizerDao;
    private final PlaceDao placeDao;
    private final AdminDao adminDao;
    private final UserDao userDao;
    private final int maxTicketBatchSize;

    /**
     * Creates a service with DAO dependencies required by concert workflows.
     *
     * @param concertDao DAO used to persist and query concerts
     * @param organizerDao DAO used to resolve organizers
     * @param placeDao DAO used to resolve places
     * @param adminDao DAO used to resolve admins
     * @param userDao DAO used to resolve authenticated users
     * @param maxTicketBatchSize maximum number of tickets created at concert creation
     */
    public ConcertServiceImpl(
            ConcertDao concertDao,
            OrganizerDao organizerDao,
            PlaceDao placeDao,
            AdminDao adminDao,
            UserDao userDao,
            int maxTicketBatchSize
    ) {
        this.concertDao = concertDao;
        this.organizerDao = organizerDao;
        this.placeDao = placeDao;
        this.adminDao = adminDao;
        this.userDao = userDao;
        this.maxTicketBatchSize = maxTicketBatchSize;
    }

    /**
     * Creates a new concert proposal after validating request consistency.
     *
     * @param request creation payload from organizer side
     * @return created concert mapped to response DTO
     */
    @Override
    public ResponseConcertDetailsDto createConcert(CreateConcertRequestDto request) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }

        if (request.organizerId() == null) {
            throw new BadRequestException("organizerId is required");
        }

        if (request.placeId() == null) {
            throw new BadRequestException("placeId is required");
        }

        if (request.date() == null) {
            throw new BadRequestException("date is required");
        }
        if (request.ticketUnitPrice() == null) {
            throw new BadRequestException("ticketUnitPrice is required");
        }
        if (request.ticketQuantity() == null) {
            throw new BadRequestException("ticketQuantity is required");
        }
        if (request.ticketQuantity() <= 0) {
            throw new BadRequestException("ticketQuantity must be greater than 0");
        }
        if (request.ticketQuantity() > maxTicketBatchSize) {
            throw new BadRequestException("ticketQuantity must be <= " + maxTicketBatchSize);
        }
        if (request.ticketUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("ticketUnitPrice must be greater than 0");
        }
        if (request.ticketUnitPrice().scale() > 2) {
            throw new BadRequestException("ticketUnitPrice must have at most 2 decimal places");
        }

        if (request.date().isBefore(Instant.now())) {
            throw new BadRequestException("date must be in the future");
        }

        String title = normalizeRequired("title", request.title());
        String artist = request.artist() == null ? null : request.artist().trim();

        Organizer organizer = organizerDao.findOne(request.organizerId());
        if (organizer == null) {
            throw new NotFoundException("Organizer not found");
        }

        Place place = placeDao.findOne(request.placeId());
        if (place == null) {
            throw new NotFoundException("Place not found");
        }
        if (place.getCapacity() != null && request.ticketQuantity() > place.getCapacity()) {
            throw new BadRequestException("ticketQuantity must be <= place capacity");
        }

        Instant bookingWindowStart = request.date().minus(PLACE_BOOKING_DURATION);
        Instant bookingWindowEnd = request.date().plus(PLACE_BOOKING_DURATION);

        boolean placeAlreadyBooked = concertDao.existsPlaceBookingConflict(
                place.getId(),
                bookingWindowStart,
                bookingWindowEnd,
                PLACE_BOOKING_BLOCKING_STATUSES
        );

        if (placeAlreadyBooked) {
            throw new ClientErrorException(
                    "Place already booked for the requested time slot",
                    Response.Status.CONFLICT
            );
        }

        Concert concert = new Concert();
        concert.setTitle(title);
        concert.setArtist(artist);
        concert.setDate(request.date());
        concert.setOrganizer(organizer);
        concert.setPlace(place);
        concert.setStatus(ConcertStatus.PENDING_VALIDATION);
        concert.setTickets(createInitialTickets(concert, request.ticketQuantity(), request.ticketUnitPrice()));

        concertDao.save(concert);

        return toResponse(concert);
    }

    /**
     * Validates a pending concert and publishes it.
     *
     * @param concertId identifier of the target concert
     * @param authenticatedAdminEmail authenticated admin email extracted from JWT context
     * @return updated concert mapped to response DTO
     */
    @Override
    public ResponseConcertDetailsDto validateConcert(
            UUID concertId,
            String authenticatedAdminEmail
    ) {
        if (concertId == null) {
            throw new BadRequestException("concertId is required");
        }

        String email = normalizeRequired("authenticatedAdminEmail", authenticatedAdminEmail)
                .toLowerCase(Locale.ROOT);

        Concert concert = concertDao.findOne(concertId);
        if (concert == null) {
            throw new NotFoundException("Concert not found");
        }

        if (concert.getStatus() != ConcertStatus.PENDING_VALIDATION) {
            throw new ClientErrorException(
                    "Concert is not pending validation",
                    Response.Status.CONFLICT
            );
        }

        User authenticatedUser = userDao.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Admin not found"));
        if (!(authenticatedUser instanceof Admin authenticatedAdmin)) {
            throw new ForbiddenException("User is not an admin");
        }

        Admin admin = adminDao.findOne(authenticatedAdmin.getId());
        if (admin == null) {
            throw new NotFoundException("Admin not found");
        }

        concert.setAdmin(admin);
        concert.setStatus(ConcertStatus.PUBLISHED);

        Concert updated = concertDao.update(concert);
        return toResponse(updated);
    }

    /**
     * Returns all published concerts.
     *
     * @return published concerts
     */
    @Override
    public List<ResponseConcertDetailsDto> getPublicConcerts() {
        return concertDao.findByStatus(ConcertStatus.PUBLISHED)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Returns concerts waiting for validation.
     *
     * @return pending concerts
     */
    @Override
    public List<ResponseConcertDetailsDto> getPendingConcerts() {
        return concertDao.findPendingConcertDetailsProjection();
    }

    /**
     * Returns published concerts with place details.
     *
     * @return published concert projections
     */
    @Override
    public List<ResponseConcertPlaceDto> getPublishedConcertsWithPlace() {
        return concertDao.findPublishedConcertsWithPlaceProjection();
    }

    /**
     * Returns concerts created by the authenticated organizer.
     *
     * @param authenticatedOrganizerEmail method parameter
     * @return organizer concert projections
     */
    @Override
    public List<ResponseOrganizerConcertDto> getOrganizerConcerts(String authenticatedOrganizerEmail) {
        String email = normalizeRequired("authenticatedOrganizerEmail", authenticatedOrganizerEmail)
                .toLowerCase(Locale.ROOT);

        User authenticatedUser = userDao.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Organizer not found"));
        if (!(authenticatedUser instanceof Organizer organizer)) {
            throw new ForbiddenException("User is not an organizer");
        }

        return concertDao.findOrganizerConcertsProjection(organizer.getId());
    }

    private ResponseConcertDetailsDto toResponse(Concert concert) {
        UUID organizerId = concert.getOrganizer() != null ? concert.getOrganizer().getId() : null;
        UUID adminId = concert.getAdmin() != null ? concert.getAdmin().getId() : null;
        UUID placeId = concert.getPlace() != null ? concert.getPlace().getId() : null;
        String status = concert.getStatus() != null ? concert.getStatus().name() : null;

        return new ResponseConcertDetailsDto(
                concert.getId(),
                concert.getTitle(),
                concert.getArtist(),
                concert.getDate(),
                status,
                organizerId,
                adminId,
                placeId,
                concert.getCreatedAt(),
                concert.getUpdatedAt()
        );
    }

    private List<Ticket> createInitialTickets(Concert concert, int quantity, BigDecimal unitPrice) {
        BigDecimal normalizedPrice = unitPrice.setScale(2, RoundingMode.HALF_UP);
        List<Ticket> tickets = new ArrayList<>(quantity);

        for (int i = 0; i < quantity; i++) {
            Ticket ticket = new Ticket();
            ticket.setConcert(concert);
            ticket.setPrice(normalizedPrice);
            ticket.setBarcode(generateBarcode());
            tickets.add(ticket);
        }

        return tickets;
    }

    private String generateBarcode() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase(Locale.ROOT);
    }
}
