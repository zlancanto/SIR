package jpa.services.impl;

import jpa.dao.abstracts.AdminDao;
import jpa.services.interfaces.AdminService;

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
}
