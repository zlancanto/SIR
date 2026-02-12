package jpa.services.impl;

import jpa.dao.abstracts.AdminDao;
import jpa.services.interfaces.AdminService;

public class AdminServiceImpl implements AdminService {
    private final AdminDao adminDao;

    public AdminServiceImpl(AdminDao adminDao) {
        this.adminDao = adminDao;
    }
}
