package jpa.dto.concert;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.UUID;

/**
 * Published concert projection including venue details.
 */
@Schema(name = "ConcertPlace")
public record ResponseConcertPlaceDto(
        @Schema(description = "Concert identifier", type = "string", format = "uuid")
        UUID concertId,
        @Schema(description = "Concert title", example = "Live Rennes")
        String concertTitle,
        @Schema(description = "Concert artist", example = "Band X", nullable = true)
        String concertArtist,
        @Schema(description = "Concert date", type = "string", format = "date-time")
        Instant concertDate,
        @Schema(description = "Venue name", example = "Le Liberte", nullable = true)
        String placeName,
        @Schema(description = "Venue address", example = "1 Esplanade Charles de Gaulle", nullable = true)
        String placeAddress,
        @Schema(description = "Venue zip code", example = "35000", nullable = true)
        Integer placeZipCode,
        @Schema(description = "Venue city", example = "Rennes", nullable = true)
        String placeCity,
        @Schema(description = "Venue capacity", example = "5500", nullable = true)
        Integer placeCapacity,
        @Schema(description = "Available places", example = "1200", nullable = true)
        Integer availablePlaces
) {
}
