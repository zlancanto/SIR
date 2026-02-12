package jpa.controllers;

import jpa.services.interfaces.OrganizerService;

public class OrganizerController {
    private final OrganizerService organizerService;

    public OrganizerController(OrganizerService organizerService) {
        this.organizerService = organizerService;
    }
}
