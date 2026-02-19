package jpa.controllers;

import jpa.services.interfaces.PlaceService;

/**
 * REST controller exposing PlaceController endpoints.
 */
public class PlaceController {
    private final PlaceService placeService;

    /**
     * Creates a new instance of PlaceController.
     *
     * @param placeService method parameter
     */
    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }
}
