package jpa.dto.concert;

import java.util.UUID;

/**
 * Request payload used by an admin to validate a pending concert.
 */
public record ValidateConcertRequestDto(
        UUID adminId
) {
}
