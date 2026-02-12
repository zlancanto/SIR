package jpa.controllers;

import jpa.services.interfaces.PlaceService;

public class PlaceController {
    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }
}
