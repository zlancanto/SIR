package jpa.controllers;

import jpa.services.ConcertService;

public class ConcertController {
    private final ConcertService concertService;

    public ConcertController(ConcertService concertService) {
        this.concertService = concertService;
    }
}
