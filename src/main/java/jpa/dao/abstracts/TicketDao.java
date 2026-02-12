package jpa.dao.abstracts;

import jpa.dao.generic.AbstractJpaDao;
import jpa.entities.Ticket;

import java.util.List;
import java.util.UUID;

public abstract class TicketDao extends AbstractJpaDao<UUID, Ticket> {
    protected abstract List<Ticket> findByPriceLowerThan(double maxPrice);
    protected abstract List<Ticket> findAvailableTickets();
}
