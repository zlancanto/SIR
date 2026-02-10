package jpa.services;

import jpa.dao.impl.AdminDao;

public class AdminService {
    private final AdminDao adminDao;

    public AdminService(AdminDao adminDao) {
        this.adminDao = adminDao;
    }
}
