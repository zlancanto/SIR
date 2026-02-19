package jpa.controllers;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jpa.config.Instance;
import jpa.dto.user.CreateAdminRequestDto;
import jpa.dto.user.CreateUserRequestDto;
import jpa.dto.user.ResponseUserDto;
import jpa.services.interfaces.UserRegistrationService;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserController {

    public static final String ADMIN_REGISTRATION_HEADER = "X-Admin-Registration-Key";

    private final UserRegistrationService userRegistrationService;

    public UserController() {
        this.userRegistrationService = Instance.USER_REGISTRATION_SERVICE;
    }

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
