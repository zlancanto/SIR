package jpa.services.impl;

import jpa.dao.abstracts.PlaceDao;
import jpa.dto.place.ResponsePlaceDto;
import jpa.services.interfaces.PlaceService;

import java.util.List;

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

    /**
     * Returns all places in response projection format.
     *
     * @return place projections
     */
    @Override
    public List<ResponsePlaceDto> getAllPlaces() {
        return placeDao.findAllPlaceProjections();
    }
}
