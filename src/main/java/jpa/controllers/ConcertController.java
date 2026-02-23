package jpa.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jpa.config.Instance;
import jpa.dto.concert.CreateConcertRequestDto;
import jpa.dto.concert.ResponseConcertDetailsDto;
import jpa.dto.concert.ResponseOrganizerConcertDto;
import jpa.dto.concert.ResponseConcertPlaceDto;
import jpa.dto.exceptions.ResponseExceptionDto;
import jpa.services.interfaces.ConcertService;

import java.util.List;
import java.util.UUID;

import static jpa.utils.Security.resolveAuthenticatedEmail;

/**
 * REST endpoints for concert creation, validation and listing.
 */
@Path("/concerts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Concerts", description = "Concert creation, moderation and publication endpoints.")
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
    @RolesAllowed("ROLE_ORGANIZER")
    @Operation(
            summary = "Create a concert proposal",
            description = "Creates a concert in PENDING_VALIDATION status. "
                    + "The place must exist and be available in the requested 3-hour booking window. "
                    + "Initial tickets are generated from ticketUnitPrice and ticketQuantity."
    )
    @RequestBody(
            required = true,
            description = "Concert creation payload",
            content = @Content(schema = @Schema(implementation = CreateConcertRequestDto.class))
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Concert proposal created",
                    content = @Content(schema = @Schema(implementation = ResponseConcertDetailsDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload",
                    content = @Content(schema = @Schema(implementation = ResponseExceptionDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Organizer or place not found",
                    content = @Content(schema = @Schema(implementation = ResponseExceptionDto.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Requested place is already booked for this time slot",
                    content = @Content(schema = @Schema(implementation = ResponseExceptionDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Missing or invalid bearer access token",
                    content = @Content(schema = @Schema(implementation = ResponseExceptionDto.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User does not have organizer privileges",
                    content = @Content(schema = @Schema(implementation = ResponseExceptionDto.class))
            )
    })
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
     * @return HTTP 200 with updated concert details
     */
    @POST
    @Path("/{concertId}/validate")
    @RolesAllowed("ROLE_ADMIN")
    @Operation(
            summary = "Validate a pending concert",
            description = "Validates a PENDING_VALIDATION concert and publishes it using the authenticated admin."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Concert validated and published",
                    content = @Content(schema = @Schema(implementation = ResponseConcertDetailsDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request parameter",
                    content = @Content(schema = @Schema(implementation = ResponseExceptionDto.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User does not have admin privileges",
                    content = @Content(schema = @Schema(implementation = ResponseExceptionDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Concert or admin not found",
                    content = @Content(schema = @Schema(implementation = ResponseExceptionDto.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Concert is not pending validation",
                    content = @Content(schema = @Schema(implementation = ResponseExceptionDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Missing or invalid bearer access token",
                    content = @Content(schema = @Schema(implementation = ResponseExceptionDto.class))
            )
    })
    public Response validate(
            @Parameter(
                    in = ParameterIn.PATH,
                    required = true,
                    description = "Concert identifier",
                    schema = @Schema(type = "string", format = "uuid")
            )
            @PathParam("concertId") UUID concertId,
            @Context SecurityContext securityContext
    ) {
        String msgException = "Authenticated admin is required";
        String authenticatedAdminEmail = resolveAuthenticatedEmail(securityContext, msgException);
        ResponseConcertDetailsDto validated = concertService.validateConcert(concertId, authenticatedAdminEmail);
        return Response.ok(validated).build();
    }

    /**
     * Lists concerts available to public users.
     *
     * @return HTTP 200 with published concerts
     */
    @GET
    @Path("/public")
    @PermitAll
    @Operation(
            summary = "List published concerts",
            description = "Returns concerts currently visible to public users."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Published concerts",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = ResponseConcertDetailsDto.class))
                    )
            )
    })
    public Response getPublicConcerts() {
        List<ResponseConcertDetailsDto> concerts = concertService.getPublicConcerts();
        return Response.ok(concerts).build();
    }

    /**
     * Lists published concerts with place details and available seats.
     *
     * @return HTTP 200 with published concerts projection
     */
    @GET
    @Path("/public/places")
    @PermitAll
    @Operation(
            summary = "List published concerts with place details",
            description = "Returns published concerts in projection format with place metadata and available places."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Published concerts with place details",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = ResponseConcertPlaceDto.class))
                    )
            )
    })
    public Response getPublishedConcertsWithPlace() {
        List<ResponseConcertPlaceDto> concerts = concertService.getPublishedConcertsWithPlace();
        return Response.ok(concerts).build();
    }

    /**
     * Lists concerts created by the authenticated organizer.
     *
     * @param securityContext authenticated security context
     * @return HTTP 200 with organizer concerts
     */
    @GET
    @Path("/me")
    @RolesAllowed("ROLE_ORGANIZER")
    @Operation(
            summary = "List organizer concerts",
            description = "Returns concerts created by the authenticated organizer with place and ticket aggregates."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Organizer concerts",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = ResponseOrganizerConcertDto.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Missing or invalid bearer access token",
                    content = @Content(schema = @Schema(implementation = ResponseExceptionDto.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User does not have organizer privileges",
                    content = @Content(schema = @Schema(implementation = ResponseExceptionDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Organizer not found",
                    content = @Content(schema = @Schema(implementation = ResponseExceptionDto.class))
            )
    })
    public Response getMyConcerts(@Context SecurityContext securityContext) {
        String msgException = "Authenticated organizer is required";
        String authenticatedOrganizerEmail = resolveAuthenticatedEmail(securityContext, msgException);
        List<ResponseOrganizerConcertDto> concerts = concertService.getOrganizerConcerts(authenticatedOrganizerEmail);
        return Response.ok(concerts).build();
    }

    /**
     * Lists concerts still waiting for validation.
     *
     * @return HTTP 200 with pending concerts
     */
    @GET
    @Path("/pending")
    @RolesAllowed("ROLE_ADMIN")
    @Operation(
            summary = "List pending concerts",
            description = "Returns concerts awaiting validation for authenticated admins."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Pending concerts",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = ResponseConcertDetailsDto.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User does not have admin privileges",
                    content = @Content(schema = @Schema(implementation = ResponseExceptionDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Missing or invalid bearer access token",
                    content = @Content(schema = @Schema(implementation = ResponseExceptionDto.class))
            )
    })
    public Response getPendingConcerts() {
        List<ResponseConcertDetailsDto> concerts = concertService.getPendingConcerts();
        return Response.ok(concerts).build();
    }
}
