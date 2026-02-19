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
 * REST controller exposing ConcertController endpoints.
 */
@Path("/concerts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConcertController {

    private final ConcertService concertService;

    /**
     * Creates a new instance of ConcertController.
     */
    public ConcertController() {
        this.concertService = Instance.CONCERT_SERVICE;
    }

    /**
     * Executes create operation.
     *
     * @return operation result
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
     * Executes validate operation.
     *
     * @param concertId method parameter
     * @param adminActionKey method parameter
     * @param request method parameter
     * @return operation result
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
     * Executes getPublicConcerts operation.
     *
     * @return operation result
     */
    @GET
    @Path("/public")
    public Response getPublicConcerts() {
        List<ResponseConcertDetailsDto> concerts = concertService.getPublicConcerts();
        return Response.ok(concerts).build();
    }

    /**
     * Executes getPendingConcerts operation.
     *
     * @param adminActionKey method parameter
     * @return operation result
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
