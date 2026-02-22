package jpa.dto.ticket;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * Payload used by a customer to buy tickets for a concert.
 *
 * @param concertId identifier of the target concert
 * @param quantity number of tickets to buy
 */
@Schema(name = "PurchaseTicketsRequest")
public record PurchaseTicketsRequestDto(
        @Schema(
                description = "Target concert identifier",
                type = "string",
                format = "uuid",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        UUID concertId,
        @Schema(
                description = "Number of tickets to buy",
                example = "2",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        Integer quantity
) {
}
