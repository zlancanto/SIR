package jpa.dao.impl;

import jakarta.persistence.EntityManager;
import jpa.EntityManagerHelper;
import jpa.dao.abstracts.TicketDao;
import jpa.entities.Ticket;

import java.util.List;

public class TicketDaoImpl extends TicketDao {
    private final static EntityManager entityManager = EntityManagerHelper.getEntityManager();

    @Override
    public List<Ticket> findByPriceLowerThan(double maxPrice) {
        String jpql = "SELECT t FROM Ticket t WHERE t.price <= :maxPrice";
        return entityManager.createQuery(jpql, Ticket.class)
                .setParameter("maxPrice", maxPrice)
                .getResultList();
    }

    @Override
    public List<Ticket> findAvailableTickets() {
        return entityManager.createNamedQuery("Ticket.findAvailable", Ticket.class)
                .getResultList();
    }
}
