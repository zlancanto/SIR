package jpa.dto.concert;

import io.swagger.v3.oas.annotations.media.Schema;
import jpa.enums.StatsGranularity;

/**
 * Request model used to parameterize organizer stats.
 */
@Schema(name = "RequestOrganizerConcertStats")
public record RequestOrganizerConcertStatsDto(
        @Schema(
                description = "Lower datetime bound (inclusive, ISO-8601 instant)",
                example = "2026-01-01T00:00:00Z",
                nullable = true
        )
        String from,
        @Schema(
                description = "Upper datetime bound (inclusive, ISO-8601 instant)",
                example = "2026-12-31T23:59:59Z",
                nullable = true
        )
        String to,
        @Schema(
                description = "Timeline granularity",
                example = "MONTH",
                nullable = true
        )
        StatsGranularity granularity,
        @Schema(
                description = "Top-N size for rankings",
                example = "10",
                nullable = true
        )
        Integer top,
        @Schema(
                description = "Include detailed concert list",
                example = "true",
                nullable = true
        )
        Boolean includeConcerts
) {
}
