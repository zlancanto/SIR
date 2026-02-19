package jpa.dao.impl;

import jakarta.persistence.EntityManager;
import jpa.dao.abstracts.UserDao;
import jpa.entities.User;

import java.util.List;
import java.util.Optional;

/**
 * JPA DAO implementation for UserDaoImpl.
 */
public class UserDaoImpl extends UserDao {

    /**
     * Executes findByEmail operation.
     *
     * @param email method parameter
     * @return operation result
     */
    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }

        EntityManager em = getEntityManager();
        String jpql = "SELECT u FROM User u WHERE LOWER(u.email) = LOWER(:email)";
        List<User> result = em.createQuery(jpql, User.class)
                .setParameter("email", email.trim())
                .setMaxResults(1)
                .getResultList();

        return result.stream().findFirst();
    }
}
