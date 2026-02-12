package jpa.config;

import jpa.controllers.OrganizerController;
import jpa.dao.abstracts.OrganizerDao;
import jpa.services.interfaces.OrganizerService;

public final class OrganizerSingleton {
    private static OrganizerSingleton daoInstance;
    private static OrganizerSingleton serviceInstance;
    private static OrganizerSingleton controllerInstance;
    private OrganizerDao daoValue;
    private OrganizerService serviceValue;
    private OrganizerController controllerValue;

    private OrganizerSingleton(OrganizerDao daoValue) {
        this.daoValue = daoValue;
    }

    private OrganizerSingleton(OrganizerService serviceValue) {
        this.serviceValue = serviceValue;
    }

    private OrganizerSingleton(OrganizerController controllerValue) {
        this.controllerValue = controllerValue;
    }

    public static OrganizerSingleton getDaoInstance(OrganizerDao daoValue) {
        if (daoInstance == null) {
            daoInstance = new OrganizerSingleton(daoValue);
        }
        return daoInstance;
    }

    public static OrganizerSingleton getServiceInstance(OrganizerService serviceValue) {
        if (serviceInstance == null) {
            serviceInstance = new OrganizerSingleton(serviceValue);
        }
        return serviceInstance;
    }

    public static OrganizerSingleton getControllerInstance(OrganizerController controllerValue) {
        if (controllerInstance == null) {
            controllerInstance = new OrganizerSingleton(controllerValue);
        }
        return controllerInstance;
    }
}
