package jpa.controllers;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jpa.dto.admin.ResponseAdminDto;
import jpa.services.interfaces.AdminService;

@Path("/admins")
@Produces(MediaType.APPLICATION_JSON)
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GET
    public Response ping() {
        return Response.ok(new ResponseAdminDto("admin endpoint ready")).build();
    }
}
