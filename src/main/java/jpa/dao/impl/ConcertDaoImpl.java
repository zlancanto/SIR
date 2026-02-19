package jpa.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jpa.dao.abstracts.ConcertDao;
import jpa.entities.Concert;

import java.time.Instant;
import java.util.List;

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
}
