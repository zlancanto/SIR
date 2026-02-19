package jpa.controllers;

import jpa.services.interfaces.OrganizerService;

/**
 * REST controller exposing OrganizerController endpoints.
 */
public class OrganizerController {
    private final OrganizerService organizerService;

    /**
     * Creates a new instance of OrganizerController.
     *
     * @param organizerService method parameter
     */
    public OrganizerController(OrganizerService organizerService) {
        this.organizerService = organizerService;
    }
}
