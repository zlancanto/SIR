package jpa.controllers;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jpa.dto.concert.ResponseConcertDto;
import jpa.services.interfaces.ConcertService;

/**
 * REST controller exposing ConcertController endpoints.
 */
@Path("/concerts")
@Produces(MediaType.APPLICATION_JSON)
public class ConcertController {
    private final ConcertService concertService;

    /**
     * Creates a new instance of ConcertController.
     *
     * @param concertService method parameter
     */
    public ConcertController(ConcertService concertService) {
        this.concertService = concertService;
    }

    /**
     * Executes ping operation.
     *
     * @return operation result
     */
    @GET
    public Response ping() {
        return Response.ok(new ResponseConcertDto("concert endpoint ready")).build();
    }
}
