package jpa.dto.concert;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * Payload used to validate a pending concert.
 *
 * @param adminId identifier of the admin performing the validation action
 */
@Schema(name = "ValidateConcertRequest")
public record ValidateConcertRequestDto(
        @Schema(
                description = "Identifier of the validating admin",
                type = "string",
                format = "uuid",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        UUID adminId
) {
}
