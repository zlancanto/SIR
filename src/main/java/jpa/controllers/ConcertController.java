package jpa.controllers;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jpa.dto.concert.ResponseConcertDto;
import jpa.services.interfaces.ConcertService;

@Path("/concerts")
@Produces(MediaType.APPLICATION_JSON)
public class ConcertController {
    private final ConcertService concertService;

    public ConcertController(ConcertService concertService) {
        this.concertService = concertService;
    }

    @GET
    public Response ping() {
        return Response.ok(new ResponseConcertDto("concert endpoint ready")).build();
    }
}
