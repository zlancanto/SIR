package jpa.dao.impl;

import jakarta.persistence.EntityManager;
import jpa.dao.abstracts.CustomerDao;
import jpa.entities.Customer;

import java.util.List;
import java.util.Optional;

/**
 * JPA DAO implementation for CustomerDaoImpl.
 */
public class CustomerDaoImpl extends CustomerDao {
    /**
     * Executes findByEmail operation.
     *
     * @param email method parameter
     * @return operation result
     */
    @Override
    public Optional<Customer> findByEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }

        EntityManager em = getEntityManager();
        String jpql = "SELECT c FROM Customer c WHERE LOWER(c.email) = LOWER(:email)";
        List<Customer> result = em.createQuery(jpql, Customer.class)
                .setParameter("email", email.trim())
                .setMaxResults(1)
                .getResultList();

        return result.stream().findFirst();
    }
}
