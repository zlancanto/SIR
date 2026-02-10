package jpa.config;

import jpa.controllers.PlaceController;
import jpa.dao.impl.PlaceDao;
import jpa.services.PlaceService;

public final class PlaceSingleton {
    private static PlaceSingleton daoInstance;
    private static PlaceSingleton serviceInstance;
    private static PlaceSingleton controllerInstance;
    private PlaceDao daoValue;
    private PlaceService serviceValue;
    private PlaceController controllerValue;

    private PlaceSingleton(PlaceDao daoValue) {
        this.daoValue = daoValue;
    }

    private PlaceSingleton(PlaceService serviceValue) {
        this.serviceValue = serviceValue;
    }

    private PlaceSingleton(PlaceController controllerValue) {
        this.controllerValue = controllerValue;
    }

    public static PlaceSingleton getDaoInstance(PlaceDao daoValue) {
        if (daoInstance == null) {
            daoInstance = new PlaceSingleton(daoValue);
        }
        return daoInstance;
    }

    public static PlaceSingleton getServiceInstance(PlaceService serviceValue) {
        if (serviceInstance == null) {
            serviceInstance = new PlaceSingleton(serviceValue);
        }
        return serviceInstance;
    }

    public static PlaceSingleton getControllerInstance(PlaceController controllerValue) {
        if (controllerInstance == null) {
            controllerInstance = new PlaceSingleton(controllerValue);
        }
        return controllerInstance;
    }
}
