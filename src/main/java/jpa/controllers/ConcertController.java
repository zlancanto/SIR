package jpa.controllers;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jpa.config.AdminConfig;
import jpa.config.Instance;
import jpa.dto.concert.CreateConcertRequestDto;
import jpa.dto.concert.ResponseConcertDetailsDto;
import jpa.dto.concert.ValidateConcertRequestDto;
import jpa.services.interfaces.ConcertService;

import java.util.List;
import java.util.UUID;

/**
 * REST endpoints for concert creation, validation and listing.
 */
@Path("/concerts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConcertController {

    private final ConcertService concertService;

    /**
     * Builds the controller with default application wiring.
     */
    public ConcertController() {
        this.concertService = Instance.CONCERT_SERVICE;
    }

    /**
     * Creates a concert proposal.
     *
     * @param request creation payload sent by the organizer
     * @return HTTP 201 with created concert details
     */
    @POST
    @Path("/create")
    public Response create(CreateConcertRequestDto request) {
        ResponseConcertDetailsDto created = concertService.createConcert(request);
        return Response.status(Response.Status.CREATED)
                .entity(created)
                .build();
    }

    /**
     * Validates a pending concert.
     *
     * @param concertId concert identifier from path
     * @param adminActionKey privileged key from header
     * @param request payload containing admin identifier
     * @return HTTP 200 with updated concert details
     */
    @POST
    @Path("/{concertId}/validate")
    public Response validate(
            @PathParam("concertId") UUID concertId,
            @HeaderParam(AdminConfig.ADMIN_ACTION_HEADER) String adminActionKey,
            ValidateConcertRequestDto request
    ) {
        ResponseConcertDetailsDto validated = concertService.validateConcert(concertId, request, adminActionKey);
        return Response.ok(validated).build();
    }

    /**
     * Lists concerts available to public users.
     *
     * @return HTTP 200 with published concerts
     */
    @GET
    @Path("/public")
    public Response getPublicConcerts() {
        List<ResponseConcertDetailsDto> concerts = concertService.getPublicConcerts();
        return Response.ok(concerts).build();
    }

    /**
     * Lists concerts still waiting for validation.
     *
     * @param adminActionKey privileged key from header
     * @return HTTP 200 with pending concerts
     */
    @GET
    @Path("/pending")
    public Response getPendingConcerts(
            @HeaderParam(AdminConfig.ADMIN_ACTION_HEADER) String adminActionKey
    ) {
        List<ResponseConcertDetailsDto> concerts = concertService.getPendingConcerts(adminActionKey);
        return Response.ok(concerts).build();
    }
}
