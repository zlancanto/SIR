package jpa.dto.concert;

import java.time.Instant;
import java.util.UUID;

/**
 * Payload used by an organizer to request concert creation.
 *
 * @param title concert title as displayed publicly
 * @param artist optional artist name
 * @param date scheduled date and time of the concert
 * @param organizerId identifier of the organizer creating the concert
 * @param placeId identifier of the venue where the concert will be held
 */
public record CreateConcertRequestDto(
        String title,
        String artist,
        Instant date,
        UUID organizerId,
        UUID placeId
) {
}
