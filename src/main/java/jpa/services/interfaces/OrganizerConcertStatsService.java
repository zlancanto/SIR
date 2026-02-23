package jpa.services.interfaces;

import jpa.dto.concert.RequestOrganizerConcertStatsDto;
import jpa.dto.concert.ResponseOrganizerConcertStatsDto;

/**
 * Service contract dedicated to organizer concert statistics.
 */
public interface OrganizerConcertStatsService {

    /**
     * Returns dashboard statistics for concerts created by the authenticated organizer.
     *
     * @param authenticatedOrganizerEmail organizer email extracted from JWT context
     * @param request query params for range/granularity/rankings
     * @return detailed stats payload for frontend graphs
     */
    ResponseOrganizerConcertStatsDto getOrganizerConcertStats(
            String authenticatedOrganizerEmail,
            RequestOrganizerConcertStatsDto request
    );
}
