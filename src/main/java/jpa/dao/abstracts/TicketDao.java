package jpa.dao.abstracts;

import jpa.dao.generic.AbstractJpaDao;
import jpa.entities.Customer;
import jpa.entities.Ticket;

import java.util.List;
import java.util.UUID;

/**
 * Abstract DAO contract for TicketDao.
 */
public abstract class TicketDao extends AbstractJpaDao<UUID, Ticket> {
    protected TicketDao() {
        super(Ticket.class);
    }

    /**
     * Executes findByPriceLowerThan operation.
     *
     * @param maxPrice method parameter
     * @return operation result
     */
    public abstract List<Ticket> findByPriceLowerThan(double maxPrice);

    /**
     * Executes findAvailableTickets operation.
     *
     * @return operation result
     */
    public abstract List<Ticket> findAvailableTickets();

    /**
     * Atomically reserves available tickets for a customer on one concert.
     *
     * @param concertId target concert identifier
     * @param customer ticket buyer
     * @param quantity expected number of tickets to reserve
     * @return reserved tickets, or empty list if not enough availability
     */
    public abstract List<Ticket> reserveAvailableTickets(UUID concertId, Customer customer, int quantity);
}
