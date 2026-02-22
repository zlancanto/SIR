package jpa.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jpa.config.Instance;
import jpa.dto.exceptions.ResponseExceptionDto;
import jpa.dto.ticket.PurchaseTicketsRequestDto;
import jpa.dto.ticket.ResponseTicketDetailsDto;
import jpa.services.interfaces.TicketService;

import java.util.List;

import static jpa.utils.Security.resolveAuthenticatedEmail;

/**
 * REST controller exposing TicketController endpoints.
 */
@Path("/tickets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Tickets", description = "Ticket purchase endpoints.")
public class TicketController {
    private final TicketService ticketService;

    /**
     * Creates a new instance of TicketController.
     */
    public TicketController() {
        this.ticketService = Instance.TICKET_SERVICE;
    }

    /**
     * Purchases one or more tickets for a concert.
     *
     * @param request purchase payload
     * @param securityContext authenticated security context
     * @return HTTP 200 with purchased tickets
     */
    @POST
    @Path("/purchase")
    @RolesAllowed("ROLE_CUSTOMER")
    @Operation(
            summary = "Purchase tickets for a concert",
            description = "Buys one or more available tickets for a published concert."
    )
    @RequestBody(
            required = true,
            description = "Ticket purchase payload",
            content = @Content(schema = @Schema(implementation = PurchaseTicketsRequestDto.class))
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Tickets purchased successfully",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = ResponseTicketDetailsDto.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload",
                    content = @Content(schema = @Schema(implementation = ResponseExceptionDto.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Missing or invalid bearer access token",
                    content = @Content(schema = @Schema(implementation = ResponseExceptionDto.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User does not have customer privileges",
                    content = @Content(schema = @Schema(implementation = ResponseExceptionDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Customer or concert not found",
                    content = @Content(schema = @Schema(implementation = ResponseExceptionDto.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Concert state or ticket availability conflict",
                    content = @Content(schema = @Schema(implementation = ResponseExceptionDto.class))
            )
    })
    public Response purchase(PurchaseTicketsRequestDto request, @Context SecurityContext securityContext) {
        String msgException = "Authenticated customer is required";
        String authenticatedCustomerEmail = resolveAuthenticatedEmail(securityContext, msgException);
        List<ResponseTicketDetailsDto> purchased = ticketService.purchaseTickets(request, authenticatedCustomerEmail);
        return Response.ok(purchased).build();
    }
}
