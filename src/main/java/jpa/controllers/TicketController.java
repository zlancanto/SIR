package jpa.controllers;

import jpa.services.interfaces.TicketService;

/**
 * REST controller exposing TicketController endpoints.
 */
public class TicketController {
    private final TicketService ticketService;

    /**
     * Creates a new instance of TicketController.
     *
     * @param ticketService method parameter
     */
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }
}
