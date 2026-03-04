package jpa.services.impl;

import jpa.dao.abstracts.AdminDao;
import jpa.dto.admin.ResponseAdminSummaryDto;
import jpa.entities.Admin;
import jpa.services.interfaces.AdminService;

import java.util.List;

/**
 * Service implementation AdminServiceImpl.
 */
public class AdminServiceImpl implements AdminService {
    private final AdminDao adminDao;

    /**
     * Creates a new instance of AdminServiceImpl.
     *
     * @param adminDao method parameter
     */
    public AdminServiceImpl(AdminDao adminDao) {
        this.adminDao = adminDao;
    }

    @Override
    public List<ResponseAdminSummaryDto> getAllAdmins() {
        return adminDao.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ResponseAdminSummaryDto toResponse(Admin admin) {
        return new ResponseAdminSummaryDto(
                admin.getId(),
                admin.getEmail(),
                admin.getFirstName(),
                admin.getLastName(),
                "ROLE_ADMIN",
                admin.getCreatedAt()
        );
    }
}
