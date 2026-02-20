package jpa.dto.concert;

import io.swagger.v3.oas.annotations.media.Schema;

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
@Schema(name = "ConcertDetails")
public record ResponseConcertDetailsDto(
        @Schema(description = "Concert identifier", type = "string", format = "uuid")
        UUID id,
        @Schema(description = "Concert title", example = "Nuit Electro Rennes")
        String title,
        @Schema(description = "Artist name", example = "Daft Punk Tribute", nullable = true)
        String artist,
        @Schema(description = "Scheduled date and time", type = "string", format = "date-time")
        Instant date,
        @Schema(description = "Workflow status", example = "PENDING_VALIDATION")
        String status,
        @Schema(description = "Organizer identifier", type = "string", format = "uuid")
        UUID organizerId,
        @Schema(description = "Admin identifier when validated", type = "string", format = "uuid", nullable = true)
        UUID adminId,
        @Schema(description = "Venue identifier", type = "string", format = "uuid")
        UUID placeId,
        @Schema(description = "Creation timestamp", type = "string", format = "date-time")
        Instant createdAt,
        @Schema(description = "Last update timestamp", type = "string", format = "date-time")
        Instant updatedAt
) {
}
