package jpa.security.interfaces;

import jpa.dto.security.AccessTokenClaimsDto;
import jpa.entities.User;

import java.time.Instant;

/**
 * Defines JWT and refresh-token primitives used by authentication components.
 */
public interface AccessTokenService {

    /**
     * Creates a signed JWT access token for the given user identity.
     *
     * @param user authenticated user
     * @param role resolved user role to embed in claims
     * @param issuedAt token issue instant
     * @param expiresAt token expiration instant
     * @return compact JWT string
     */
    String createAccessToken(User user, String role, Instant issuedAt, Instant expiresAt);

    /**
     * Verifies a JWT access token signature and required claims.
     *
     * @param rawToken compact JWT provided by the client
     * @return validated token claims
     */
    AccessTokenClaimsDto verifyAccessToken(String rawToken);

    /**
     * Generates a cryptographically random opaque refresh token.
     *
     * @return raw refresh token to return to the client
     */
    String generateRefreshToken();

    /**
     * Hashes an opaque refresh token before persistence.
     *
     * @param rawToken raw refresh token provided by the client
     * @return deterministic hash representation suitable for storage
     */
    String hashRefreshToken(String rawToken);

    /**
     * Returns configured access-token lifetime in seconds.
     *
     * @return access-token TTL
     */
    long getAccessTokenTtlSeconds();

    /**
     * Returns configured refresh-token lifetime in seconds.
     *
     * @return refresh-token TTL
     */
    long getRefreshTokenTtlSeconds();
}
