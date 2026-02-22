package jpa.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jpa.config.Instance;
import jpa.dto.exceptions.ResponseExceptionDto;
import jpa.dto.user.CreateAdminRequestDto;
import jpa.dto.user.CreateUserRequestDto;
import jpa.dto.user.ResponseUserDto;
import jpa.services.interfaces.UserRegistrationService;

/**
 * REST controller exposing UserController endpoints.
 */
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Users", description = "Public and privileged user registration endpoints.")
public class UserController {

    public static final String ADMIN_REGISTRATION_HEADER = "X-Admin-Registration-Key";

    private final UserRegistrationService userRegistrationService;

    /**
     * Creates a new instance of UserController.
     */
    public UserController() {
        this.userRegistrationService = Instance.USER_REGISTRATION_SERVICE;
    }

    /**
     * Executes register operation.
     *
     * @param request method parameter
     * @return operation result
     */
    @POST
    @Path("/register")
    @PermitAll
    @Operation(
            summary = "Register a customer or organizer",
            description = "Public signup endpoint. Allowed roles are CUSTOMER and ORGANIZER."
    )
    @RequestBody(
            required = true,
            description = "Public user registration payload",
            content = @Content(schema = @Schema(implementation = CreateUserRequestDto.class))
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User created",
                    content = @Content(schema = @Schema(implementation = ResponseUserDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload",
                    content = @Content(schema = @Schema(implementation = ResponseExceptionDto.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Self-registration is forbidden for requested role",
                    content = @Content(schema = @Schema(implementation = ResponseExceptionDto.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email already exists",
                    content = @Content(schema = @Schema(implementation = ResponseExceptionDto.class))
            )
    })
    public Response register(CreateUserRequestDto request) {
        ResponseUserDto created = userRegistrationService.register(request);
        return Response.status(Response.Status.CREATED)
                .entity(created)
                .build();
    }

    @POST
    @Path("/register/admin")
    @RolesAllowed("ROLE_ADMIN")
    @Operation(
            summary = "Register an admin",
            description = "Privileged admin signup endpoint guarded by header key."
    )
    @RequestBody(
            required = true,
            description = "Admin registration payload",
            content = @Content(schema = @Schema(implementation = CreateAdminRequestDto.class))
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Admin created",
                    content = @Content(schema = @Schema(implementation = ResponseUserDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload",
                    content = @Content(schema = @Schema(implementation = ResponseExceptionDto.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Missing or invalid admin registration key",
                    content = @Content(schema = @Schema(implementation = ResponseExceptionDto.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Email already exists",
                    content = @Content(schema = @Schema(implementation = ResponseExceptionDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Missing or invalid bearer access token",
                    content = @Content(schema = @Schema(implementation = ResponseExceptionDto.class))
            )
    })
    public Response registerAdmin(
            @Parameter(
                    name = ADMIN_REGISTRATION_HEADER,
                    in = ParameterIn.HEADER,
                    required = true,
                    description = "Privileged key required to create admin users",
                    schema = @Schema(type = "string")
            )
            @HeaderParam(ADMIN_REGISTRATION_HEADER) String adminRegistrationKey,
            CreateAdminRequestDto request
    ) {
        ResponseUserDto created = userRegistrationService.registerAdmin(request, adminRegistrationKey);
        return Response.status(Response.Status.CREATED)
                .entity(created)
                .build();
    }
}
