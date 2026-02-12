package jpa.services.impl;

import jpa.dao.abstracts.PlaceDao;
import jpa.services.interfaces.PlaceService;

public class PlaceServiceImpl implements PlaceService {
    private final PlaceDao placeDao;

    public PlaceServiceImpl(PlaceDao placeDao) {
        this.placeDao = placeDao;
    }
}
