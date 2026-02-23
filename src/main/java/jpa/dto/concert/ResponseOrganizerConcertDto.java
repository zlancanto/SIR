package jpa.dto.concert;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Organizer concert listing projection with place and ticket aggregates.
 */
@Schema(name = "OrganizerConcert")
public record ResponseOrganizerConcertDto(
        @Schema(description = "Concert title", example = "Nuit Electro Rennes")
        String concertTitle,
        @Schema(description = "Concert artist", example = "Daft Punk Tribute", nullable = true)
        String concertArtist,
        @Schema(description = "Concert creation timestamp", type = "string", format = "date-time")
        Instant concertCreatedAt,
        @Schema(description = "Concert scheduled date", type = "string", format = "date-time")
        Instant concertDate,
        @Schema(description = "Concert workflow status", example = "PUBLISHED")
        String concertStatus,
        @Schema(description = "Place address", example = "1 Esplanade Charles de Gaulle", nullable = true)
        String placeAddress,
        @Schema(description = "Place zip code", example = "35000", nullable = true)
        Integer placeZipCode,
        @Schema(description = "Place city", example = "Rennes", nullable = true)
        String placeCity,
        @Schema(description = "Place capacity", example = "5500", nullable = true)
        Integer placeCapacity,
        @Schema(description = "Sold ticket count", example = "120")
        Integer ticketSold,
        @Schema(description = "Total ticket count", example = "500")
        Integer ticketQuantity
) {
}
