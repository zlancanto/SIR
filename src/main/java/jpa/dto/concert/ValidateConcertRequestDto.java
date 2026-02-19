package jpa.dto.concert;

import java.util.UUID;

/**
 * Payload used to validate a pending concert.
 *
 * @param adminId identifier of the admin performing the validation action
 */
public record ValidateConcertRequestDto(
        UUID adminId
) {
}
