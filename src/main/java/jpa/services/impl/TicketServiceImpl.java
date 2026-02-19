package jpa.services.impl;

import jpa.dao.abstracts.TicketDao;
import jpa.services.interfaces.TicketService;

/**
 * Service implementation TicketServiceImpl.
 */
public class TicketServiceImpl implements TicketService {
    private final TicketDao ticketDao;

    /**
     * Creates a new instance of TicketServiceImpl.
     *
     * @param ticketDao method parameter
     */
    public TicketServiceImpl(TicketDao ticketDao) {
        this.ticketDao = ticketDao;
    }
}
