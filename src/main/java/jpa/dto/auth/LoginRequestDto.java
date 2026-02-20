package jpa.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request payload used to authenticate a user with email and password.
 *
 * @param email user login email
 * @param password raw password provided by the client
 */
@Schema(name = "LoginRequest")
public record LoginRequestDto(
        @Schema(
                description = "User email address",
                format = "email",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "alice@example.com"
        )
        String email,
        @Schema(
                description = "Raw account password",
                requiredMode = Schema.RequiredMode.REQUIRED,
                example = "myStrongPass123"
        )
        String password
) {
}
