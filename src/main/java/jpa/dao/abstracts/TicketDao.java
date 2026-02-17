package jpa.dao.abstracts;

import jpa.dao.generic.AbstractJpaDao;
import jpa.entities.Ticket;

import java.util.List;
import java.util.UUID;

public abstract class TicketDao extends AbstractJpaDao<UUID, Ticket> {
    protected TicketDao() {
        super(Ticket.class);
    }

    public abstract List<Ticket> findByPriceLowerThan(double maxPrice);
    public abstract List<Ticket> findAvailableTickets();
}
