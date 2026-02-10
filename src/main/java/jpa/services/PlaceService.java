package jpa.services;

import jpa.dao.impl.PlaceDao;

public class PlaceService {
    private final PlaceDao placeDao;

    public PlaceService(PlaceDao placeDao) {
        this.placeDao = placeDao;
    }
}
