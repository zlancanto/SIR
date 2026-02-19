package jpa.controllers;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jpa.config.Instance;
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
    public Response register(CreateUserRequestDto request) {
        ResponseUserDto created = userRegistrationService.register(request);
        return Response.status(Response.Status.CREATED)
                .entity(created)
                .build();
    }

    @POST
    @Path("/register/admin")
    public Response registerAdmin(
            @HeaderParam(ADMIN_REGISTRATION_HEADER) String adminRegistrationKey,
            CreateAdminRequestDto request
    ) {
        ResponseUserDto created = userRegistrationService.registerAdmin(request, adminRegistrationKey);
        return Response.status(Response.Status.CREATED)
                .entity(created)
                .build();
    }
}
