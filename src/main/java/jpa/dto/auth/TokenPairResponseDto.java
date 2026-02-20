package jpa.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Response payload containing a freshly issued access/refresh token pair.
 *
 * @param accessToken signed JWT access token
 * @param refreshToken opaque refresh token to rotate on next refresh
 * @param tokenType token type expected by Authorization header consumers
 * @param accessTokenExpiresAt expiration instant of access token
 * @param refreshTokenExpiresAt expiration instant of refresh token
 */
@Schema(name = "TokenPairResponse")
public record TokenPairResponseDto(
        @Schema(description = "Signed JWT access token")
        String accessToken,
        @Schema(description = "Opaque refresh token")
        String refreshToken,
        @Schema(description = "Token type", example = "Bearer")
        String tokenType,
        @Schema(description = "Access-token expiration instant", type = "string", format = "date-time")
        Instant accessTokenExpiresAt,
        @Schema(description = "Refresh-token expiration instant", type = "string", format = "date-time")
        Instant refreshTokenExpiresAt
) {
}
