package jpa.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jpa.config.Instance;
import jpa.dto.auth.LoginRequestDto;
import jpa.dto.auth.RefreshTokenRequestDto;
import jpa.dto.auth.TokenPairResponseDto;
import jpa.dto.exceptions.ResponseExceptionDto;
import jpa.services.interfaces.AuthService;

/**
 * REST endpoints for authentication and token refresh.
 */
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Auth", description = "Authentication and token rotation endpoints.")
public class AuthController {

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
        return Response.ok(response).build();
    }

    /**
     * Rotates refresh token and returns a fresh access/refresh pair.
     *
     * @param request refresh payload
     * @return HTTP 200 with new token pair
     */
    @POST
    @Path("/refresh")
    @Operation(summary = "Refresh and rotate token pair")
    @RequestBody(
            required = true,
            description = "Current refresh token payload",
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
    public Response refresh(RefreshTokenRequestDto request) {
        TokenPairResponseDto response = authService.refresh(request);
        return Response.ok(response).build();
    }
}
