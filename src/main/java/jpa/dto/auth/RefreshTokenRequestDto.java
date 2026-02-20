package jpa.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request payload used to rotate a refresh token.
 *
 * @param refreshToken opaque refresh token issued at login/refresh time
 */
@Schema(name = "RefreshTokenRequest")
public record RefreshTokenRequestDto(
        @Schema(
                description = "Opaque refresh token",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String refreshToken
) {
}
