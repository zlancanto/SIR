package jpa.services;

import jpa.dao.impl.TicketDao;

public class TicketService {
    private TicketDao ticketDao;

    public TicketService(TicketDao ticketDao) {
        this.ticketDao = ticketDao;
    }
}
