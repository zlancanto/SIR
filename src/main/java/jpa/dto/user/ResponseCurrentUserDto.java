package jpa.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Payload returned for the authenticated user profile endpoint.
 *
 * @param email user email
 * @param firstName user first name
 * @param lastName user last name
 * @param role persisted role value
 * @param createdAt account creation timestamp
 */
@Schema(name = "CurrentUserResponse")
public record ResponseCurrentUserDto(
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
        String role,
        @Schema(description = "Account creation timestamp", type = "string", format = "date-time")
        Instant createdAt
) {
}
