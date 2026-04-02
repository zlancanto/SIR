package jpa.dao.impl;

import jakarta.persistence.EntityManager;
import jpa.dao.abstracts.RefreshTokenDao;
import jpa.entities.RefreshToken;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA DAO implementation for refresh token access.
 */
public class RefreshTokenDaoImpl extends RefreshTokenDao {

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<RefreshToken> findValidByHash(String tokenHash, Instant now) {
        if (tokenHash == null || tokenHash.isBlank() || now == null) {
            return Optional.empty();
        }

        EntityManager em = getEntityManager();
        String jpql = """
                SELECT rt
                FROM RefreshToken rt
                WHERE rt.tokenHash = :tokenHash
                  AND rt.revokedAt IS NULL
                  AND rt.expiresAt > :now
                """;

        List<RefreshToken> result = em.createQuery(jpql, RefreshToken.class)
                .setParameter("tokenHash", tokenHash)
                .setParameter("now", now)
                .setMaxResults(1)
                .getResultList();

        return result.stream().findFirst();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<RefreshToken> findLatestByUserId(UUID userId) {
        if (userId == null) {
            return Optional.empty();
        }

        EntityManager em = getEntityManager();
        String jpql = """
                SELECT rt
                FROM RefreshToken rt
                WHERE rt.user.id = :userId
                ORDER BY rt.createdAt DESC
                """;

        List<RefreshToken> result = em.createQuery(jpql, RefreshToken.class)
                .setParameter("userId", userId)
                .setMaxResults(1)
                .getResultList();

        return result.stream().findFirst();
    }
}
