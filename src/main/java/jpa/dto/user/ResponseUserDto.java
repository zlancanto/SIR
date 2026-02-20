package jpa.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * Data transfer object ResponseUserDto.
 */
@Schema(name = "UserResponse")
public record ResponseUserDto(
        @Schema(description = "User identifier", type = "string", format = "uuid")
        UUID id,
        @Schema(description = "User email address", format = "email", example = "alice@example.com")
        String email,
        @Schema(description = "User first name", example = "Alice")
        String firstName,
        @Schema(description = "User last name", example = "Martin")
        String lastName,
        @Schema(
                description = "Persisted role value",
                allowableValues = {"ROLE_CUSTOMER", "ROLE_ORGANIZER", "ROLE_ADMIN"},
                example = "ROLE_CUSTOMER"
        )
        String role
) {
}
