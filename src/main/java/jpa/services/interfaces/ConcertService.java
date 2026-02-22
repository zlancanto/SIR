package jpa.services.interfaces;

import jpa.dto.concert.CreateConcertRequestDto;
import jpa.dto.concert.ResponseConcertDetailsDto;

import java.util.List;
import java.util.UUID;

/**
 * Application service for concert lifecycle operations.
 *
 * <p>The lifecycle is:
 * pending creation request from an organizer, then validation by an admin,
 * then publication for public listing.</p>
 */
public interface ConcertService {
    /**
     * Creates a new concert in {@code PENDING_VALIDATION} status.
     *
     * @param request creation payload containing title, date, organizer and place references
     * @return created concert with persisted identifiers and timestamps
     */
    ResponseConcertDetailsDto createConcert(CreateConcertRequestDto request);

    /**
     * Validates a pending concert and publishes it.
     *
     * @param concertId identifier of the concert to validate
     * @param authenticatedAdminEmail authenticated admin email extracted from JWT context
     * @return updated concert after status transition to {@code PUBLISHED}
     */
    ResponseConcertDetailsDto validateConcert(UUID concertId, String authenticatedAdminEmail);

    /**
     * Lists concerts visible to public users.
     *
     * @return concerts currently published
     */
    List<ResponseConcertDetailsDto> getPublicConcerts();

    /**
     * Lists concerts awaiting admin validation.
     *
     * @return concerts with status {@code PENDING_VALIDATION}
     */
    List<ResponseConcertDetailsDto> getPendingConcerts();
}
