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
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jpa.config.AdminConfig;
import jpa.config.Instance;
import jpa.dto.concert.CreateConcertRequestDto;
import jpa.dto.concert.ResponseConcertDetailsDto;
import jpa.dto.concert.ValidateConcertRequestDto;
import jpa.dto.exceptions.ResponseExceptionDto;
import jpa.services.interfaces.ConcertService;

import java.util.List;
import java.util.UUID;

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
    @Operation(
            summary = "Create a concert proposal",
            description = "Creates a concert in PENDING_VALIDATION status. "
                    + "The place must exist and be available in the requested 3-hour booking window."
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
     * @param adminActionKey privileged key from header
     * @param request payload containing admin identifier
     * @return HTTP 200 with updated concert details
     */
    @POST
    @Path("/{concertId}/validate")
    @Operation(
            summary = "Validate a pending concert",
            description = "Validates a PENDING_VALIDATION concert and publishes it."
    )
    @RequestBody(
            required = true,
            description = "Validation payload containing admin identifier",
            content = @Content(schema = @Schema(implementation = ValidateConcertRequestDto.class))
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Concert validated and published",
                    content = @Content(schema = @Schema(implementation = ResponseConcertDetailsDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload",
                    content = @Content(schema = @Schema(implementation = ResponseExceptionDto.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Missing or invalid admin action key",
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
            @Parameter(
                    name = AdminConfig.ADMIN_ACTION_HEADER,
                    in = ParameterIn.HEADER,
                    required = true,
                    description = "Privileged key required for moderation actions",
                    schema = @Schema(type = "string")
            )
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
     * Lists concerts still waiting for validation.
     *
     * @param adminActionKey privileged key from header
     * @return HTTP 200 with pending concerts
     */
    @GET
    @Path("/pending")
    @Operation(
            summary = "List pending concerts",
            description = "Returns concerts awaiting validation. Requires admin action key."
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
                    description = "Missing or invalid admin action key",
                    content = @Content(schema = @Schema(implementation = ResponseExceptionDto.class))
            )
    })
    public Response getPendingConcerts(
            @Parameter(
                    name = AdminConfig.ADMIN_ACTION_HEADER,
                    in = ParameterIn.HEADER,
                    required = true,
                    description = "Privileged key required for moderation actions",
                    schema = @Schema(type = "string")
            )
            @HeaderParam(AdminConfig.ADMIN_ACTION_HEADER) String adminActionKey
    ) {
        List<ResponseConcertDetailsDto> concerts = concertService.getPendingConcerts(adminActionKey);
        return Response.ok(concerts).build();
    }
}
