package jpa.services.impl;

import jpa.dao.abstracts.PlaceDao;
import jpa.services.interfaces.PlaceService;

/**
 * Service implementation PlaceServiceImpl.
 */
public class PlaceServiceImpl implements PlaceService {
    private final PlaceDao placeDao;

    /**
     * Creates a new instance of PlaceServiceImpl.
     *
     * @param placeDao method parameter
     */
    public PlaceServiceImpl(PlaceDao placeDao) {
        this.placeDao = placeDao;
    }
}
