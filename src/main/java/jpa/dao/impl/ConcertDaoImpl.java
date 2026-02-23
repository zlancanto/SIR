package jpa.dao.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jpa.dao.abstracts.ConcertDao;
import jpa.dto.concert.ResponseConcertDetailsDto;
import jpa.dto.concert.ResponseConcertPlaceDto;
import jpa.dto.concert.ResponseOrganizerConcertDto;
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

    /**
     * Executes findPublishedConcertsWithPlaceProjection operation.
     *
     * @return operation result
     */
    @Override
    public List<ResponseConcertPlaceDto> findPublishedConcertsWithPlaceProjection() {
        EntityManager em = getEntityManager();
        String jpql = """
                SELECT
                    c.id,
                    c.title,
                    c.artist,
                    c.date,
                    p.name,
                    p.address,
                    p.zipCode,
                    p.city,
                    p.capacity,
                    COALESCE(SUM(CASE WHEN t.sold = true THEN 1 ELSE 0 END), 0)
                FROM Concert c
                LEFT JOIN c.place p
                LEFT JOIN c.tickets t
                WHERE c.status = :status
                GROUP BY c.id, c.title, c.artist, c.date, p.name, p.address, p.zipCode, p.city, p.capacity
                ORDER BY c.date ASC
                """;

        List<Object[]> rows = em.createQuery(jpql, Object[].class)
                .setParameter("status", ConcertStatus.PUBLISHED)
                .getResultList();

        return rows.stream()
                .map(this::toConcertPlaceProjection)
                .toList();
    }

    /**
     * Executes findPendingConcertDetailsProjection operation.
     *
     * @return operation result
     */
    @Override
    public List<ResponseConcertDetailsDto> findPendingConcertDetailsProjection() {
        EntityManager em = getEntityManager();
        String jpql = """
                SELECT
                    c.id,
                    c.title,
                    c.artist,
                    c.date,
                    c.status,
                    o.id,
                    a.id,
                    p.id,
                    c.createdAt,
                    c.updatedAt
                FROM Concert c
                LEFT JOIN c.organizer o
                LEFT JOIN c.admin a
                LEFT JOIN c.place p
                WHERE c.status = :status
                ORDER BY c.date ASC
                """;

        List<Object[]> rows = em.createQuery(jpql, Object[].class)
                .setParameter("status", ConcertStatus.PENDING_VALIDATION)
                .getResultList();

        return rows.stream()
                .map(this::toPendingConcertDetailsProjection)
                .toList();
    }

    /**
     * Executes findOrganizerConcertsProjection operation.
     *
     * @param organizerId method parameter
     * @return operation result
     */
    @Override
    public List<ResponseOrganizerConcertDto> findOrganizerConcertsProjection(UUID organizerId) {
        if (organizerId == null) {
            return List.of();
        }

        EntityManager em = getEntityManager();
        String jpql = """
                SELECT
                    c.title,
                    c.artist,
                    c.createdAt,
                    c.date,
                    c.status,
                    p.address,
                    p.zipCode,
                    p.city,
                    p.capacity,
                    COALESCE(SUM(CASE WHEN t.sold = true THEN 1 ELSE 0 END), 0),
                    COUNT(t.id)
                FROM Concert c
                JOIN c.organizer o
                LEFT JOIN c.place p
                LEFT JOIN c.tickets t
                WHERE o.id = :organizerId
                GROUP BY c.id, c.title, c.artist, c.createdAt, c.date, c.status, p.address, p.zipCode, p.city, p.capacity
                ORDER BY c.createdAt DESC
                """;

        List<Object[]> rows = em.createQuery(jpql, Object[].class)
                .setParameter("organizerId", organizerId)
                .getResultList();

        return rows.stream()
                .map(this::toOrganizerConcertProjection)
                .toList();
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

    private ResponseConcertPlaceDto toConcertPlaceProjection(Object[] row) {
        UUID concertId = (UUID) row[0];
        String concertTitle = (String) row[1];
        String concertArtist = (String) row[2];
        Instant concertDate = (Instant) row[3];
        String placeName = (String) row[4];
        String placeAddress = (String) row[5];
        Integer placeZipCode = row[6] == null ? null : ((Number) row[6]).intValue();
        String placeCity = (String) row[7];
        Integer placeCapacity = row[8] == null ? null : ((Number) row[8]).intValue();
        long soldCount = row[9] == null ? 0L : ((Number) row[9]).longValue();

        Integer placeAvailables = placeCapacity == null
                ? null
                : Math.max(0, placeCapacity - Math.toIntExact(soldCount));

        return new ResponseConcertPlaceDto(
                concertId,
                concertTitle,
                concertArtist,
                concertDate,
                placeName,
                placeAddress,
                placeZipCode,
                placeCity,
                placeCapacity,
                placeAvailables
        );
    }

    private ResponseConcertDetailsDto toPendingConcertDetailsProjection(Object[] row) {
        UUID concertId = (UUID) row[0];
        String concertTitle = (String) row[1];
        String concertArtist = (String) row[2];
        Instant concertDate = (Instant) row[3];
        ConcertStatus concertStatus = (ConcertStatus) row[4];
        UUID organizerId = (UUID) row[5];
        UUID adminId = (UUID) row[6];
        UUID placeId = (UUID) row[7];
        Instant createdAt = (Instant) row[8];
        Instant updatedAt = (Instant) row[9];

        return new ResponseConcertDetailsDto(
                concertId,
                concertTitle,
                concertArtist,
                concertDate,
                concertStatus != null ? concertStatus.name() : null,
                organizerId,
                adminId,
                placeId,
                createdAt,
                updatedAt
        );
    }

    private ResponseOrganizerConcertDto toOrganizerConcertProjection(Object[] row) {
        String concertTitle = (String) row[0];
        String concertArtist = (String) row[1];
        Instant concertCreatedAt = (Instant) row[2];
        Instant concertDate = (Instant) row[3];
        ConcertStatus concertStatus = (ConcertStatus) row[4];
        String placeAddress = (String) row[5];
        Integer placeZipCode = row[6] == null ? null : ((Number) row[6]).intValue();
        String placeCity = (String) row[7];
        Integer placeCapacity = row[8] == null ? null : ((Number) row[8]).intValue();
        Integer ticketSold = row[9] == null ? 0 : ((Number) row[9]).intValue();
        Integer ticketQuantity = row[10] == null ? 0 : ((Number) row[10]).intValue();

        return new ResponseOrganizerConcertDto(
                concertTitle,
                concertArtist,
                concertCreatedAt,
                concertDate,
                concertStatus != null ? concertStatus.name() : null,
                placeAddress,
                placeZipCode,
                placeCity,
                placeCapacity,
                ticketSold,
                ticketQuantity
        );
    }
}
