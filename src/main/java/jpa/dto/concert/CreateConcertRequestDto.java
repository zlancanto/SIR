package jpa.dto.concert;

import java.time.Instant;
import java.util.UUID;

/**
 * Request payload used by organizers to create a concert.
 */
public record CreateConcertRequestDto(
        String title,
        String artist,
        Instant date,
        UUID organizerId,
        UUID placeId
) {
}
