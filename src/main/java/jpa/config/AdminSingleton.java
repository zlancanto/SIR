package jpa.config;

import jpa.controllers.AdminController;
import jpa.dao.abstracts.AdminDao;
import jpa.services.interfaces.AdminService;

public final class AdminSingleton {
    private static AdminSingleton daoInstance;
    private static AdminSingleton serviceInstance;
    private static AdminSingleton controllerInstance;
    private AdminDao daoValue;
    private AdminService serviceValue;
    private AdminController controllerValue;

    private AdminSingleton(AdminDao daoValue) {
        this.daoValue = daoValue;
    }

    private AdminSingleton(AdminService serviceValue) {
        this.serviceValue = serviceValue;
    }

    private AdminSingleton(AdminController controllerValue) {
        this.controllerValue = controllerValue;
    }

    public static AdminSingleton getDaoInstance(AdminDao daoValue) {
        if (daoInstance == null) {
            daoInstance = new AdminSingleton(daoValue);
        }
        return daoInstance;
    }

    public static AdminSingleton getServiceInstance(AdminService serviceValue) {
        if (serviceInstance == null) {
            serviceInstance = new AdminSingleton(serviceValue);
        }
        return serviceInstance;
    }

    public static AdminSingleton getControllerInstance(AdminController controllerValue) {
        if (controllerInstance == null) {
            controllerInstance = new AdminSingleton(controllerValue);
        }
        return controllerInstance;
    }
}
