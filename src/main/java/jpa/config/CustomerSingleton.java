package jpa.config;

import jpa.controllers.CustomerController;
import jpa.dao.abstracts.CustomerDao;
import jpa.services.interfaces.CustomerService;

public final class CustomerSingleton {
    private static CustomerSingleton daoInstance;
    private static CustomerSingleton serviceInstance;
    private static CustomerSingleton controllerInstance;
    private CustomerDao daoValue;
    private CustomerService serviceValue;
    private CustomerController controllerValue;

    private CustomerSingleton(CustomerDao daoValue) {
        this.daoValue = daoValue;
    }

    private CustomerSingleton(CustomerService serviceValue) {
        this.serviceValue = serviceValue;
    }

    private CustomerSingleton(CustomerController controllerValue) {
        this.controllerValue = controllerValue;
    }

    public static CustomerSingleton getDaoInstance(CustomerDao daoValue) {
        if (daoInstance == null) {
            daoInstance = new CustomerSingleton(daoValue);
        }
        return daoInstance;
    }

    public static CustomerSingleton getServiceInstance(CustomerService serviceValue) {
        if (serviceInstance == null) {
            serviceInstance = new CustomerSingleton(serviceValue);
        }
        return serviceInstance;
    }

    public static CustomerSingleton getControllerInstance(CustomerController controllerValue) {
        if (controllerInstance == null) {
            controllerInstance = new CustomerSingleton(controllerValue);
        }
        return controllerInstance;
    }
}
