package jpa.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data transfer object CreateUserRequestDto.
 */
@Schema(name = "CreateUserRequest")
public record CreateUserRequestDto(
        @Schema(
                description = "User email address",
                format = "email",
                example = "alice@example.com",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String email,
        @Schema(
                description = "Raw password (minimum 8 characters)",
                example = "myStrongPass123",
                minLength = 8,
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String password,
        @Schema(description = "First name", example = "Alice", requiredMode = Schema.RequiredMode.REQUIRED)
        String firstName,
        @Schema(description = "Last name", example = "Martin", requiredMode = Schema.RequiredMode.REQUIRED)
        String lastName,
        @Schema(
                description = "Public self-registration role",
                allowableValues = {"CUSTOMER", "ORGANIZER"},
                example = "CUSTOMER",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String role
) {
}
