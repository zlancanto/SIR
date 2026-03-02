package jpa.dto.place;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * Lightweight place projection returned by listing endpoint.
 */
@Schema(name = "Place")
public record ResponsePlaceDto(
        @Schema(description = "Place identifier", type = "string", format = "uuid")
        UUID placeId,
        @Schema(description = "Place name", example = "Accor Arena")
        String placeName,
        @Schema(description = "Place address", example = "8 Boulevard de Bercy")
        String placeAddress,
        @Schema(description = "Place city", example = "Paris")
        String placeCity,
        @Schema(description = "Place zip code", example = "75012")
        Integer placeZipCode,
        @Schema(description = "Place capacity", example = "20300")
        Integer placeCapacity
) {
}
