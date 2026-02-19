package jpa.dao.impl;

import jakarta.persistence.EntityManager;
import jpa.dao.abstracts.TicketDao;
import jpa.entities.Ticket;

import java.util.List;

/**
 * JPA DAO implementation for TicketDaoImpl.
 */
public class TicketDaoImpl extends TicketDao {

    /**
     * Executes findByPriceLowerThan operation.
     *
     * @param maxPrice method parameter
     * @return operation result
     */
    @Override
    public List<Ticket> findByPriceLowerThan(double maxPrice) {
        EntityManager em = getEntityManager();
        String jpql = "SELECT t FROM Ticket t WHERE t.price <= :maxPrice";
        return em.createQuery(jpql, Ticket.class)
                .setParameter("maxPrice", maxPrice)
                .getResultList();
    }

    /**
     * Executes findAvailableTickets operation.
     *
     * @return operation result
     */
    @Override
    public List<Ticket> findAvailableTickets() {
        EntityManager em = getEntityManager();
        return em.createNamedQuery("Ticket.findAvailable", Ticket.class)
                .getResultList();
    }
}
