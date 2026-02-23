package jpa.dao.abstracts;

import jpa.dao.generic.AbstractJpaDao;
import jpa.dto.concert.ResponseConcertDetailsDto;
import jpa.dto.concert.ResponseConcertPlaceDto;
import jpa.entities.Concert;
import jpa.enums.ConcertStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Abstract DAO contract for ConcertDao.
 */
public abstract class ConcertDao extends AbstractJpaDao<UUID, Concert> {

    protected ConcertDao() {
        super(Concert.class);
    }

    /**
     * Executes findConcertsByDateRange operation.
     *
     * @param start method parameter
     * @param end   method parameter
     * @return operation result
     */
    public abstract List<Concert> findConcertsByDateRange(Instant start, Instant end);

    /**
     * Executes findByStatus operation.
     *
     * @param status method parameter
     * @return operation result
     */
    public abstract List<Concert> findByStatus(ConcertStatus status);

    /**
     * Returns published concerts with place projection and available seats.
     *
     * @return projected published concerts
     */
    public abstract List<ResponseConcertPlaceDto> findPublishedConcertsWithPlaceProjection();

    /**
     * Returns pending concerts as details projection.
     *
     * @return projected pending concerts
     */
    public abstract List<ResponseConcertDetailsDto> findPendingConcertDetailsProjection();

    /**
     * Returns true when a place already has a concert in the requested slot window.
     *
     * @param placeId venue identifier
     * @param windowStartExclusive lower bound of the start-time window (exclusive)
     * @param windowEndExclusive upper bound of the start-time window (exclusive)
     * @param blockingStatuses statuses that should block a new reservation
     * @return true if a conflicting concert exists
     */
    public abstract boolean existsPlaceBookingConflict(
            UUID placeId,
            Instant windowStartExclusive,
            Instant windowEndExclusive,
            List<ConcertStatus> blockingStatuses
    );
}
