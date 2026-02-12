package jpa.controllers;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jpa.services.interfaces.AdminService;

@Produces(MediaType.APPLICATION_JSON)
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
}
