package jpa.dto.concert;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Flat projection returned by admin moderation listing endpoints.
 *
 * @param concertTitle concert title
 * @param concertArtist optional concert artist
 * @param concertCreatedAt concert creation timestamp
 * @param concertDate scheduled date of the concert
 * @param placeAddress venue street address
 * @param placeZipCode venue zip code
 * @param placeCity venue city
 * @param placeCapacity venue capacity
 * @param ticketQuantity total number of tickets attached to the concert
 * @param organizerFistName organizer first name
 * @param organizerLastName organizer last name
 */
@Schema(name = "AdminConcertModeration")
public record ResponseAdminConcertModerationDto(
        @Schema(description = "Concert title", example = "Nuit Electro Rennes")
        String concertTitle,
        @Schema(description = "Concert artist", example = "Daft Punk Tribute", nullable = true)
        String concertArtist,
        @Schema(description = "Concert creation timestamp", type = "string", format = "date-time")
        Instant concertCreatedAt,
        @Schema(description = "Concert scheduled date", type = "string", format = "date-time")
        Instant concertDate,
        @Schema(description = "Place address", example = "1 Esplanade Charles de Gaulle", nullable = true)
        String placeAddress,
        @Schema(description = "Place zip code", example = "35000", nullable = true)
        Integer placeZipCode,
        @Schema(description = "Place city", example = "Rennes", nullable = true)
        String placeCity,
        @Schema(description = "Place capacity", example = "5500", nullable = true)
        Integer placeCapacity,
        @Schema(description = "Total ticket quantity", example = "500")
        Integer ticketQuantity,
        @Schema(description = "Organizer first name", example = "Alice", nullable = true)
        String organizerFistName,
        @Schema(description = "Organizer last name", example = "Martin", nullable = true)
        String organizerLastName
) {
}
