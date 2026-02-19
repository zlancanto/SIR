package jpa.controllers;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jpa.dto.admin.ResponseAdminDto;
import jpa.services.interfaces.AdminService;

/**
 * REST controller exposing AdminController endpoints.
 */
@Path("/admins")
@Produces(MediaType.APPLICATION_JSON)
public class AdminController {
    private final AdminService adminService;

    /**
     * Creates a new instance of AdminController.
     *
     * @param adminService method parameter
     */
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Executes ping operation.
     *
     * @return operation result
     */
    @GET
    public Response ping() {
        return Response.ok(new ResponseAdminDto("admin endpoint ready")).build();
    }
}
