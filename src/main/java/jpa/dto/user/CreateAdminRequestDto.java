package jpa.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data transfer object CreateAdminRequestDto.
 */
@Schema(name = "CreateAdminRequest")
public record CreateAdminRequestDto(
        @Schema(
                description = "Admin email address",
                format = "email",
                example = "admin@example.com",
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
        @Schema(description = "Admin first name", example = "Alex", requiredMode = Schema.RequiredMode.REQUIRED)
        String firstName,
        @Schema(description = "Admin last name", example = "Dupont", requiredMode = Schema.RequiredMode.REQUIRED)
        String lastName
) {
}
