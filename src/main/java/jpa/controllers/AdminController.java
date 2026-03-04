package jpa.controllers;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jpa.config.Instance;
import jpa.dto.admin.ResponseAdminSummaryDto;
import jpa.services.interfaces.AdminService;

import java.util.List;

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
     * Creates a new instance of AdminController with default wiring.
     */
    public AdminController() {
        this.adminService = Instance.ADMIN_SERVICE;
    }

    /**
     * Lists all admins.
     *
     * @return operation result
     */
    @GET
    @Path("/all")
    @RolesAllowed("ROLE_ADMIN")
    public Response getAllAdmins() {
        List<ResponseAdminSummaryDto> admins = adminService.getAllAdmins();
        return Response.ok(admins).build();
    }
}
