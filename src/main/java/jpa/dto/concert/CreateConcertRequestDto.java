package jpa.dto.concert;

import io.swagger.v3.oas.annotations.media.Schema;

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
@Schema(name = "CreateConcertRequest")
public record CreateConcertRequestDto(
        @Schema(
                description = "Concert title shown in listings",
                example = "Nuit Electro Rennes",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String title,
        @Schema(
                description = "Optional artist name",
                example = "Daft Punk Tribute"
        )
        String artist,
        @Schema(
                description = "Scheduled concert date and time in UTC",
                type = "string",
                format = "date-time",
                example = "2026-06-15T20:00:00Z",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Instant date,
        @Schema(
                description = "Identifier of organizer creating the concert",
                type = "string",
                format = "uuid",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        UUID organizerId,
        @Schema(
                description = "Identifier of venue where the concert takes place",
                type = "string",
                format = "uuid",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        UUID placeId
) {
}
