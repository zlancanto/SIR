package jpa.dao.abstracts;

import jpa.dao.generic.AbstractJpaDao;
import jpa.dto.concert.ResponseOrganizerConcertStatsRowDto;
import jpa.entities.Concert;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Abstract DAO contract dedicated to organizer concert statistics.
 */
public abstract class OrganizerConcertStatsDao extends AbstractJpaDao<UUID, Concert> {

    protected OrganizerConcertStatsDao() {
        super(Concert.class);
    }

    /**
     * Returns flattened stats rows for concerts created by one organizer.
     *
     * @param organizerId organizer identifier
     * @param fromInclusive lower concert date bound (inclusive), nullable
     * @param toInclusive upper concert date bound (inclusive), nullable
     * @return flat rows used by service-level stats aggregations
     */
    public abstract List<ResponseOrganizerConcertStatsRowDto> findOrganizerConcertStatsRows(
            UUID organizerId,
            Instant fromInclusive,
            Instant toInclusive
    );
}
