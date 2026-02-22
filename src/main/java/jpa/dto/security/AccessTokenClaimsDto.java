package jpa.dto.security;

import java.time.Instant;
import java.util.UUID;

/**
 * Immutable payload containing validated claims extracted from a JWT access token.
 *
 * @param userId authenticated user identifier resolved from the {@code sub} claim
 * @param email authenticated user email resolved from the {@code email} claim
 * @param role resolved role name from the {@code role} claim
 * @param issuedAt token issue instant from the {@code iat} claim
 * @param expiresAt token expiration instant from the {@code exp} claim
 */
public record AccessTokenClaimsDto(
        UUID userId,
        String email,
        String role,
        Instant issuedAt,
        Instant expiresAt
) {}
