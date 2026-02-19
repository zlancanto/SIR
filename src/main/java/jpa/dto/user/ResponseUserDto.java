package jpa.dto.user;

import java.util.UUID;

public record ResponseUserDto(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String role
) {
}
