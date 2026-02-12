package jpa.config;

import jpa.controllers.TicketController;
import jpa.dao.abstracts.TicketDao;
import jpa.services.interfaces.TicketService;

public final class TicketSingleton {
    private static TicketSingleton daoInstance;
    private static TicketSingleton serviceInstance;
    private static TicketSingleton controllerInstance;
    private TicketDao daoValue;
    private TicketService serviceValue;
    private TicketController controllerValue;

    private TicketSingleton(TicketDao daoValue) {
        this.daoValue = daoValue;
    }

    private TicketSingleton(TicketService serviceValue) {
        this.serviceValue = serviceValue;
    }

    private TicketSingleton(TicketController controllerValue) {
        this.controllerValue = controllerValue;
    }

    public static TicketSingleton getDaoInstance(TicketDao daoValue) {
        if (daoInstance == null) {
            daoInstance = new TicketSingleton(daoValue);
        }
        return daoInstance;
    }

    public static TicketSingleton getServiceInstance(TicketService serviceValue) {
        if (serviceInstance == null) {
            serviceInstance = new TicketSingleton(serviceValue);
        }
        return serviceInstance;
    }

    public static TicketSingleton getControllerInstance(TicketController controllerValue) {
        if (controllerInstance == null) {
            controllerInstance = new TicketSingleton(controllerValue);
        }
        return controllerInstance;
    }
}
