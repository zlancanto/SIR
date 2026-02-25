package jpa.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jpa.config.Instance;
import jpa.dto.auth.LoginRequestDto;
import jpa.dto.auth.RefreshTokenRequestDto;
import jpa.dto.auth.TokenPairResponseDto;
import jpa.dto.exceptions.ResponseExceptionDto;
import jpa.services.interfaces.AuthService;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 * REST endpoints for authentication and token refresh.
 */
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Auth", description = "Authentication and token rotation endpoints.")
public class AuthController {

    private static final String REFRESH_TOKEN_COOKIE = "refreshToken";
    private static final String REFRESH_TOKEN_COOKIE_PATH = "/";
    private static final boolean REFRESH_TOKEN_COOKIE_SECURE = false;

    private final AuthService authService;

    /**
     * Creates a controller wired with default application dependencies.
     */
    public AuthController() {
        this.authService = Instance.AUTH_SERVICE;
    }

    /**
     * Authenticates a user and returns an access/refresh token pair.
     *
     * @param request login payload
     * @return HTTP 200 with token pair
     */
    @POST
    @Path("/login")
    @PermitAll
    @Operation(summary = "Login with email and password")
    @RequestBody(
            required = true,
            description = "Credentials payload",
            content = @Content(schema = @Schema(implementation = LoginRequestDto.class))
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Authentication succeeded",
                    content = @Content(schema = @Schema(implementation = TokenPairResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid payload",
                    content = @Content(schema = @Schema(implementation = ResponseExceptionDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = ResponseExceptionDto.class))
            )
    })
    public Response login(LoginRequestDto request) {
        TokenPairResponseDto response = authService.login(request);
        return Response.ok(response)
                .cookie(buildRefreshTokenCookie(response.refreshToken(), response.refreshTokenExpiresAt()))
                .build();
    }

    /**
     * Rotates refresh token and returns a fresh access/refresh pair.
     *
     * @param request refresh payload
     * @return HTTP 200 with new token pair
     */
    @POST
    @Path("/refresh")
    @PermitAll
    @Operation(summary = "Refresh and rotate token pair")
    @RequestBody(
            required = false,
            description = "Refresh token via cookie HttpOnly `refreshToken` (body optionnel pour compatibilite)",
            content = @Content(schema = @Schema(implementation = RefreshTokenRequestDto.class))
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "New token pair issued",
                    content = @Content(schema = @Schema(implementation = TokenPairResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid payload",
                    content = @Content(schema = @Schema(implementation = ResponseExceptionDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid or expired refresh token",
                    content = @Content(schema = @Schema(implementation = ResponseExceptionDto.class))
            )
    })
    public Response refresh(
            @CookieParam(REFRESH_TOKEN_COOKIE) String refreshTokenCookie,
            RefreshTokenRequestDto request
    ) {
        String refreshToken = resolveRefreshTokenRequired(refreshTokenCookie, request);
        TokenPairResponseDto response = authService.refresh(new RefreshTokenRequestDto(refreshToken));
        return Response.ok(response)
                .cookie(buildRefreshTokenCookie(response.refreshToken(), response.refreshTokenExpiresAt()))
                .build();
    }

    /**
     * Revokes the provided refresh token and closes the current session.
     *
     * @param request payload containing the refresh token to revoke
     * @return HTTP 204 when logout is processed
     */
    @POST
    @Path("/logout")
    @PermitAll
    @Operation(summary = "Logout current session")
    @RequestBody(
            required = false,
            description = "Revoque le refresh token (cookie HttpOnly prioritaire, body optionnel)",
            content = @Content(schema = @Schema(implementation = RefreshTokenRequestDto.class))
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Logout processed (idempotent)"
            )
    })
    public Response logout(
            @CookieParam(REFRESH_TOKEN_COOKIE) String refreshTokenCookie,
            RefreshTokenRequestDto request
    ) {
        String refreshToken = resolveRefreshTokenOptional(refreshTokenCookie, request);
        if (refreshToken != null) {
            authService.logout(new RefreshTokenRequestDto(refreshToken));
        }
        return Response.noContent()
                .cookie(clearRefreshTokenCookie())
                .build();
    }

    /**
     * Resolves a refresh token from cookie first, then request body, and fails when missing.
     *
     * @param cookieRefreshToken token from cookie
     * @param request optional request body
     * @return non-blank refresh token
     */
    private String resolveRefreshTokenRequired(String cookieRefreshToken, RefreshTokenRequestDto request) {
        String token = resolveRefreshTokenOptional(cookieRefreshToken, request);
        if (token == null) {
            throw new BadRequestException("refreshToken cookie is required");
        }
        return token;
    }

    /**
     * Resolves a refresh token from cookie first, then request body.
     *
     * @param cookieRefreshToken token from cookie
     * @param request optional request body
     * @return normalized token or {@code null} when none found
     */
    private String resolveRefreshTokenOptional(String cookieRefreshToken, RefreshTokenRequestDto request) {
        String fromCookie = normalizeToken(cookieRefreshToken);
        if (fromCookie != null) {
            return fromCookie;
        }
        return request == null ? null : normalizeToken(request.refreshToken());
    }

    /**
     * Normalizes a token value.
     *
     * @param token raw token
     * @return trimmed token or {@code null} when blank
     */
    private String normalizeToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        return token.trim();
    }

    /**
     * Builds an HttpOnly cookie carrying the current refresh token.
     *
     * @param refreshToken raw refresh token
     * @param expiresAt refresh-token expiration instant
     * @return cookie to set in response
     */
    private NewCookie buildRefreshTokenCookie(String refreshToken, Instant expiresAt) {
        int maxAge = computeMaxAgeSeconds(expiresAt);
        Date expiry = expiresAt == null ? null : Date.from(expiresAt);
        return new NewCookie(
                REFRESH_TOKEN_COOKIE,
                refreshToken,
                REFRESH_TOKEN_COOKIE_PATH,
                null,
                NewCookie.DEFAULT_VERSION,
                "Refresh token",
                maxAge,
                expiry,
                REFRESH_TOKEN_COOKIE_SECURE,
                true
        );
    }

    /**
     * Builds a cookie instruction that removes the refresh-token cookie.
     *
     * @return cookie with max-age 0
     */
    private NewCookie clearRefreshTokenCookie() {
        return new NewCookie(
                REFRESH_TOKEN_COOKIE,
                "",
                REFRESH_TOKEN_COOKIE_PATH,
                null,
                NewCookie.DEFAULT_VERSION,
                "Refresh token",
                0,
                new Date(0),
                REFRESH_TOKEN_COOKIE_SECURE,
                true
        );
    }

    /**
     * Computes remaining cookie max-age based on token expiration.
     *
     * @param expiresAt expiration instant
     * @return positive max-age in seconds, 0 when already expired, -1 when unknown
     */
    private int computeMaxAgeSeconds(Instant expiresAt) {
        if (expiresAt == null) {
            return -1;
        }
        long seconds = Duration.between(Instant.now(), expiresAt).getSeconds();
        if (seconds <= 0L) {
            return 0;
        }
        return (int) Math.min(seconds, Integer.MAX_VALUE);
    }
}
