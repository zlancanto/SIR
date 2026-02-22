package jpa.services.interfaces;

import jpa.dto.ticket.PurchaseTicketsRequestDto;
import jpa.dto.ticket.ResponseTicketDetailsDto;

import java.util.List;

/**
 * Service contract for TicketService.
 */
public interface TicketService {
    /**
     * Purchases one or more available tickets for a given concert.
     *
     * @param request purchase payload
     * @param authenticatedCustomerEmail customer email extracted from JWT context
     * @return purchased tickets
     */
    List<ResponseTicketDetailsDto> purchaseTickets(
            PurchaseTicketsRequestDto request,
            String authenticatedCustomerEmail
    );
}
