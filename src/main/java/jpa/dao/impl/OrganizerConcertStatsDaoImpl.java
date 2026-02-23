package jpa.dao.impl;

import jakarta.persistence.EntityManager;
import jpa.dao.abstracts.OrganizerConcertStatsDao;
import jpa.dto.concert.ResponseOrganizerConcertStatsRowDto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * JPA DAO implementation dedicated to organizer concert statistics.
 */
public class OrganizerConcertStatsDaoImpl extends OrganizerConcertStatsDao {

    /**
     * Executes findOrganizerConcertStatsRows operation.
     *
     * @param organizerId method parameter
     * @param fromInclusive method parameter
     * @param toInclusive method parameter
     * @return operation result
     */
    @Override
    public List<ResponseOrganizerConcertStatsRowDto> findOrganizerConcertStatsRows(
            UUID organizerId,
            Instant fromInclusive,
            Instant toInclusive
    ) {
        if (organizerId == null) {
            return List.of();
        }

        EntityManager em = getEntityManager();
        StringBuilder jpql = new StringBuilder("""
                SELECT new jpa.dto.concert.ResponseOrganizerConcertStatsRowDto(
                    c.id,
                    c.title,
                    c.artist,
                    c.createdAt,
                    c.date,
                    c.status,
                    p.name,
                    p.address,
                    p.zipCode,
                    p.city,
                    p.capacity,
                    COUNT(t.id),
                    COALESCE(SUM(CASE WHEN t.sold = true THEN 1 ELSE 0 END), 0),
                    MIN(t.price)
                )
                FROM Concert c
                JOIN c.organizer o
                LEFT JOIN c.place p
                LEFT JOIN c.tickets t
                WHERE o.id = :organizerId
                """);

        if (fromInclusive != null) {
            jpql.append(" AND c.date >= :fromInclusive");
        }
        if (toInclusive != null) {
            jpql.append(" AND c.date <= :toInclusive");
        }

        jpql.append("""
                 GROUP BY c.id, c.title, c.artist, c.createdAt, c.date, c.status,
                          p.name, p.address, p.zipCode, p.city, p.capacity
                 ORDER BY c.createdAt DESC
                """);

        var query = em.createQuery(jpql.toString(), ResponseOrganizerConcertStatsRowDto.class)
                .setParameter("organizerId", organizerId);
        if (fromInclusive != null) {
            query.setParameter("fromInclusive", fromInclusive);
        }
        if (toInclusive != null) {
            query.setParameter("toInclusive", toInclusive);
        }

        return query.getResultList();
    }
}
