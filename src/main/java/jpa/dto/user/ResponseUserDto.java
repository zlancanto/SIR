package jpa.dto.user;

import java.util.UUID;

/**
 * Data transfer object ResponseUserDto.
 */
public record ResponseUserDto(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String role
) {
}
