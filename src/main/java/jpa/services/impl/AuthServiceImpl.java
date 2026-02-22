package jpa.services.impl;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotAuthorizedException;
import jpa.dao.abstracts.RefreshTokenDao;
import jpa.dao.abstracts.UserDao;
import jpa.dto.auth.LoginRequestDto;
import jpa.dto.auth.RefreshTokenRequestDto;
import jpa.dto.auth.TokenPairResponseDto;
import jpa.entities.Admin;
import jpa.entities.Customer;
import jpa.entities.Organizer;
import jpa.entities.RefreshToken;
import jpa.entities.User;
import jpa.enums.Roles;
import jpa.security.interfaces.AccessTokenService;
import jpa.services.interfaces.AuthService;

import java.time.Instant;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static jpa.utils.StringValidation.normalizeRequired;

/**
 * Default authentication service implementation.
 *
 * <p>Supports username/password login and refresh-token rotation.</p>
 */
public class AuthServiceImpl implements AuthService {

    private final UserDao userDao;
    private final RefreshTokenDao refreshTokenDao;
    private final AccessTokenService accessTokenService;

    /**
     * Creates an authentication service with DAO and token dependencies.
     *
     * @param userDao DAO used to resolve users
     * @param refreshTokenDao DAO used to persist and validate refresh tokens
     * @param accessTokenService token creation and hashing service
     */
    public AuthServiceImpl(
            UserDao userDao,
            RefreshTokenDao refreshTokenDao,
            AccessTokenService accessTokenService
    ) {
        this.userDao = userDao;
        this.refreshTokenDao = refreshTokenDao;
        this.accessTokenService = accessTokenService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TokenPairResponseDto login(LoginRequestDto request) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }

        String email = normalizeRequired("email", request.email()).toLowerCase(Locale.ROOT);
        String password = normalizeRequired("password", request.password());

        Optional<User> userOpt = userDao.findByEmail(email);
        if (userOpt.isEmpty() || !userOpt.get().verifyPassword(password)) {
            throw new NotAuthorizedException("Invalid credentials");
        }

        return issueTokenPair(userOpt.get(), Instant.now());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TokenPairResponseDto refresh(RefreshTokenRequestDto request) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }

        String rawRefreshToken = normalizeRequired("refreshToken", request.refreshToken());
        Instant now = Instant.now();
        String tokenHash = accessTokenService.hashRefreshToken(rawRefreshToken);

        RefreshToken stored = refreshTokenDao.findValidByHash(tokenHash, now)
                .orElseThrow(() -> new NotAuthorizedException("Invalid refresh token"));

        // Rotate refresh token: current token is revoked before issuing a new pair.
        stored.setRevokedAt(now);
        refreshTokenDao.update(stored);

        UUID userId = stored.getUser() != null ? stored.getUser().getId() : null;
        if (userId == null) {
            throw new NotAuthorizedException("Invalid refresh token");
        }

        User user = userDao.findOne(userId);
        if (user == null) {
            throw new NotAuthorizedException("Invalid refresh token");
        }

        return issueTokenPair(user, now);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void logout(RefreshTokenRequestDto request) {
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }

        String rawRefreshToken = normalizeRequired("refreshToken", request.refreshToken());
        Instant now = Instant.now();
        String tokenHash = accessTokenService.hashRefreshToken(rawRefreshToken);

        // Idempotent logout: revoke when token is active; otherwise do nothing.
        refreshTokenDao.findValidByHash(tokenHash, now).ifPresent(token -> {
            token.setRevokedAt(now);
            refreshTokenDao.update(token);
        });
    }

    /**
     * Issues a new access token and refresh token for the given user.
     *
     * @param user token owner
     * @param now reference instant used for expirations
     * @return token pair payload
     */
    private TokenPairResponseDto issueTokenPair(User user, Instant now) {
        String role = resolveRole(user);
        Instant accessExpiresAt = now.plusSeconds(accessTokenService.getAccessTokenTtlSeconds());
        Instant refreshExpiresAt = now.plusSeconds(accessTokenService.getRefreshTokenTtlSeconds());

        String accessToken = accessTokenService.createAccessToken(user, role, now, accessExpiresAt);
        String rawRefreshToken = accessTokenService.generateRefreshToken();
        String hashedRefreshToken = accessTokenService.hashRefreshToken(rawRefreshToken);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(hashedRefreshToken);
        refreshToken.setExpiresAt(refreshExpiresAt);
        refreshTokenDao.save(refreshToken);

        return new TokenPairResponseDto(
                accessToken,
                rawRefreshToken,
                "Bearer",
                accessExpiresAt,
                refreshExpiresAt
        );
    }

    /**
     * Resolves persisted role string from user subtype.
     *
     * @param user persisted user entity
     * @return role name matching existing role conventions
     */
    private String resolveRole(User user) {
        if (user instanceof Admin) {
            return Roles.ROLE_ADMIN.name();
        }
        if (user instanceof Organizer) {
            return Roles.ROLE_ORGANIZER.name();
        }
        if (user instanceof Customer) {
            return Roles.ROLE_CUSTOMER.name();
        }
        throw new IllegalStateException("Unsupported user type: " + user.getClass().getSimpleName());
    }
}
