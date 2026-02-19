package jpa.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jpa.dao.abstracts.ConcertDao;
import jpa.entities.Concert;
import jpa.enums.ConcertStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * JPA DAO implementation for ConcertDaoImpl.
 */
public class ConcertDaoImpl extends ConcertDao {

    /**
     * Executes findConcertsByDateRange operation.
     *
     * @param start method parameter
     * @param end   method parameter
     * @return operation result
     */
    @Override
    public List<Concert> findConcertsByDateRange(Instant start, Instant end) {
        EntityManager em = getEntityManager();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Concert> cq = cb.createQuery(Concert.class);

        Root<Concert> concert = cq.from(Concert.class);
        Predicate dateRangePredicate = cb.between(concert.get("date"), start, end);

        cq.where(dateRangePredicate);
        cq.orderBy(cb.asc(concert.get("date")));
        return em.createQuery(cq).getResultList();
    }

    /**
     * Executes findByStatus operation.
     *
     * @param status method parameter
     * @return operation result
     */
    @Override
    public List<Concert> findByStatus(ConcertStatus status) {
        EntityManager em = getEntityManager();
        String jpql = "SELECT c FROM Concert c WHERE c.status = :status ORDER BY c.date ASC";
        return em.createQuery(jpql, Concert.class)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public boolean existsPlaceBookingConflict(
            UUID placeId,
            Instant windowStartExclusive,
            Instant windowEndExclusive,
            List<ConcertStatus> blockingStatuses
    ) {
        EntityManager em = getEntityManager();
        String jpql = """
                SELECT COUNT(c)
                FROM Concert c
                WHERE c.place.id = :placeId
                  AND c.status IN :blockingStatuses
                  AND c.date > :windowStart
                  AND c.date < :windowEnd
                """;

        Long count = em.createQuery(jpql, Long.class)
                .setParameter("placeId", placeId)
                .setParameter("blockingStatuses", blockingStatuses)
                .setParameter("windowStart", windowStartExclusive)
                .setParameter("windowEnd", windowEndExclusive)
                .getSingleResult();

        return count != null && count > 0;
    }
}
