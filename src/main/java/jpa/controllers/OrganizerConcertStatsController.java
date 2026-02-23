package jpa.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jpa.config.Instance;
import jpa.dto.concert.RequestOrganizerConcertStatsDto;
import jpa.dto.concert.ResponseOrganizerConcertStatsDto;
import jpa.dto.exceptions.ResponseExceptionDto;
import jpa.enums.StatsGranularity;
import jpa.services.interfaces.OrganizerConcertStatsService;

import static jpa.utils.Security.resolveAuthenticatedEmail;

/**
 * REST endpoints dedicated to organizer concert statistics.
 */
@Path("/stats")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Organizer Concert Stats", description = "Organizer dashboard statistics endpoints.")
public class OrganizerConcertStatsController {
    private final OrganizerConcertStatsService organizerConcertStatsService;

    /**
     * Builds the controller with default application wiring.
     */
    public OrganizerConcertStatsController() {
        this.organizerConcertStatsService = Instance.ORGANIZER_CONCERT_STATS_SERVICE;
    }

    /**
     * Returns detailed stats for concerts created by the authenticated organizer.
     *
     * @param securityContext authenticated security context
     * @param from period lower bound in ISO-8601 format
     * @param to period upper bound in ISO-8601 format
     * @param granularity timeline granularity
     * @param top ranking size
     * @param includeConcerts whether detailed concerts list should be included
     * @return HTTP 200 with organizer stats payload
     */
    @GET
    @Path("/me/concerts")
    @RolesAllowed("ROLE_ORGANIZER")
    @Operation(
            summary = "Get organizer concert stats",
            description = "Returns detailed organizer stats for dashboard charts."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Organizer concert stats",
                    content = @Content(schema = @Schema(implementation = ResponseOrganizerConcertStatsDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid query parameters",
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
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Organizer not found",
                    content = @Content(schema = @Schema(implementation = ResponseExceptionDto.class))
            )
    })
    public Response getMyConcertStats(
            @Context SecurityContext securityContext,
            @QueryParam("from") String from,
            @QueryParam("to") String to,
            @DefaultValue("MONTH") @QueryParam("granularity") StatsGranularity granularity,
            @DefaultValue("10") @QueryParam("top") Integer top,
            @DefaultValue("true") @QueryParam("includeConcerts") Boolean includeConcerts
    ) {
        String msgException = "Authenticated organizer is required";
        String authenticatedOrganizerEmail = resolveAuthenticatedEmail(securityContext, msgException);

        RequestOrganizerConcertStatsDto request = new RequestOrganizerConcertStatsDto(
                from,
                to,
                granularity,
                top,
                includeConcerts
        );

        ResponseOrganizerConcertStatsDto stats = organizerConcertStatsService.getOrganizerConcertStats(
                authenticatedOrganizerEmail,
                request
        );
        return Response.ok(stats).build();
    }
}
