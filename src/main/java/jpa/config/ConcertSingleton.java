package jpa.config;

import jpa.controllers.ConcertController;
import jpa.dao.abstracts.ConcertDao;
import jpa.services.interfaces.ConcertService;

public final class ConcertSingleton {
    private static ConcertSingleton daoInstance;
    private static ConcertSingleton serviceInstance;
    private static ConcertSingleton controllerInstance;
    private ConcertDao daoValue;
    private ConcertService serviceValue;
    private ConcertController controllerValue;

    private ConcertSingleton(ConcertDao daoValue) {
        this.daoValue = daoValue;
    }

    private ConcertSingleton(ConcertService serviceValue) {
        this.serviceValue = serviceValue;
    }

    private ConcertSingleton(ConcertController controllerValue) {
        this.controllerValue = controllerValue;
    }

    public static ConcertSingleton getDaoInstance(ConcertDao daoValue) {
        if (daoInstance == null) {
            daoInstance = new ConcertSingleton(daoValue);
        }
        return daoInstance;
    }

    public static ConcertSingleton getServiceInstance(ConcertService serviceValue) {
        if (serviceInstance == null) {
            serviceInstance = new ConcertSingleton(serviceValue);
        }
        return serviceInstance;
    }

    public static ConcertSingleton getControllerInstance(ConcertController controllerValue) {
        if (controllerInstance == null) {
            controllerInstance = new ConcertSingleton(controllerValue);
        }
        return controllerInstance;
    }
}
