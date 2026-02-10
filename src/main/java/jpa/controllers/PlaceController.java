package jpa.controllers;

import jpa.services.PlaceService;

public class PlaceController {
    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }
}
