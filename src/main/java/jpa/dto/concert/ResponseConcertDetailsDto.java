package jpa.dto.concert;

import java.time.Instant;
import java.util.UUID;

/**
 * Full concert representation returned by concert endpoints.
 *
 * @param id unique identifier of the concert
 * @param title concert title
 * @param artist optional artist name
 * @param date scheduled date and time
 * @param status workflow status (for example {@code PENDING_VALIDATION} or {@code PUBLISHED})
 * @param organizerId identifier of the organizer who created the concert
 * @param adminId identifier of the admin who validated the concert, or {@code null} if pending
 * @param placeId identifier of the venue
 * @param createdAt entity creation timestamp
 * @param updatedAt last update timestamp
 */
public record ResponseConcertDetailsDto(
        UUID id,
        String title,
        String artist,
        Instant date,
        String status,
        UUID organizerId,
        UUID adminId,
        UUID placeId,
        Instant createdAt,
        Instant updatedAt
) {
}
