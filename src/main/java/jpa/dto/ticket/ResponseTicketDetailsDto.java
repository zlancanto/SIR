package jpa.dto.ticket;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Minimal ticket representation returned by purchase endpoints.
 *
 * @param ticketId purchased ticket identifier
 * @param ticketBarcode purchased ticket barcode
 * @param ticketPrice purchased ticket price
 */
@Schema(name = "TicketDetails")
public record ResponseTicketDetailsDto(
        @Schema(description = "Ticket identifier", type = "string", format = "uuid")
        UUID ticketId,
        @Schema(description = "Ticket barcode", example = "C5A0A067378A4D85B063A3A9C806B0B7")
        String ticketBarcode,
        @Schema(description = "Ticket price", example = "49.90")
        BigDecimal ticketPrice
) {
}
