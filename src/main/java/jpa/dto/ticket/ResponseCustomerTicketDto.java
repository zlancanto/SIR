package jpa.dto.ticket;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Ticket projection for customer ticket listing.
 */
@Schema(name = "CustomerTicket")
public record ResponseCustomerTicketDto(
        @Schema(description = "Ticket identifier", type = "string", format = "uuid")
        UUID ticketId,
        @Schema(description = "Ticket price", example = "49.90")
        BigDecimal ticketPrice,
        @Schema(description = "Ticket barcode", example = "C5A0A067378A4D85B063A3A9C806B0B7")
        String ticketBarCode,
        @Schema(description = "Concert title", example = "Live Rennes")
        String concertTitle,
        @Schema(description = "Concert artist", example = "Band X", nullable = true)
        String concertArtist,
        @Schema(description = "Concert date", type = "string", format = "date-time")
        Instant concertDate,
        @Schema(description = "Place name", example = "Le Liberte", nullable = true)
        String placeName,
        @Schema(description = "Place address", example = "1 Esplanade Charles de Gaulle", nullable = true)
        String placeAddress,
        @Schema(description = "Place zip code", example = "35000", nullable = true)
        Integer placeZipCode,
        @Schema(description = "Place city", example = "Rennes", nullable = true)
        String placeCity,
        @Schema(description = "Place capacity", example = "5500", nullable = true)
        Integer placeCapacity
) {
}
