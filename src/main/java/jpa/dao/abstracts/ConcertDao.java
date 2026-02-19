package jpa.dao.abstracts;

import jpa.dao.generic.AbstractJpaDao;
import jpa.entities.Concert;

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
}
