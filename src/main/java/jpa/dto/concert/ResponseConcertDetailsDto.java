package jpa.dto.concert;

import java.time.Instant;
import java.util.UUID;

/**
 * Response payload representing the full concert state.
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
