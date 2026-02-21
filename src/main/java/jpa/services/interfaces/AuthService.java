package jpa.services.interfaces;

import jpa.dto.auth.LoginRequestDto;
import jpa.dto.auth.RefreshTokenRequestDto;
import jpa.dto.auth.TokenPairResponseDto;

/**
 * Service contract for authentication operations.
 */
public interface AuthService {

    /**
     * Authenticates a user and issues a token pair.
     *
     * @param request login payload containing credentials
     * @return newly issued token pair
     */
    TokenPairResponseDto login(LoginRequestDto request);

    /**
     * Rotates a refresh token and issues a new token pair.
     *
     * @param request payload containing current refresh token
     * @return newly issued token pair
     */
    TokenPairResponseDto refresh(RefreshTokenRequestDto request);

    /**
     * Revokes the provided refresh token to terminate the current session.
     *
     * <p>The operation is intended to be idempotent:
     * if the token is already invalid, revoked or unknown, no error is raised.</p>
     *
     * @param request payload containing the refresh token to revoke
     */
    void logout(RefreshTokenRequestDto request);
}
