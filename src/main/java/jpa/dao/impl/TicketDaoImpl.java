package jpa.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jpa.dao.abstracts.TicketDao;
import jpa.entities.Customer;
import jpa.entities.Ticket;

import java.util.List;
import java.util.UUID;

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

    /**
     * Executes reserveAvailableTickets operation.
     *
     * @param concertId method parameter
     * @param customer method parameter
     * @param quantity method parameter
     * @return operation result
     */
    @Override
    public List<Ticket> reserveAvailableTickets(UUID concertId, Customer customer, int quantity) {
        if (concertId == null || customer == null || customer.getId() == null || quantity <= 0) {
            return List.of();
        }

        return executeInTransaction(em -> {
            Customer managedCustomer = em.getReference(Customer.class, customer.getId());
            String jpql = """
                    SELECT t
                    FROM Ticket t
                    WHERE t.concert.id = :concertId
                      AND t.sold = false
                    ORDER BY t.createdAt ASC
                    """;

            List<Ticket> available = em.createQuery(jpql, Ticket.class)
                    .setParameter("concertId", concertId)
                    .setMaxResults(quantity)
                    .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .getResultList();

            if (available.size() < quantity) {
                return List.of();
            }

            for (Ticket ticket : available) {
                ticket.setSold(true);
                ticket.setCustomer(managedCustomer);
            }

            return available;
        });
    }
}
