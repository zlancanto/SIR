package jpa.services.impl;

import jpa.dao.abstracts.TicketDao;
import jpa.services.interfaces.TicketService;

public class TicketServiceImpl implements TicketService {
    private TicketDao ticketDao;

    public TicketServiceImpl(TicketDao ticketDao) {
        this.ticketDao = ticketDao;
    }
}
