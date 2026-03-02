package jpa.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jpa.config.Instance;
import jpa.dto.place.ResponsePlaceDto;
import jpa.services.interfaces.PlaceService;

import java.util.List;

/**
 * REST controller exposing PlaceController endpoints.
 */
@Path("/places")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Places", description = "Place listing endpoints.")
public class PlaceController {
    private final PlaceService placeService;

    /**
     * Creates a new instance of PlaceController.
     */
    public PlaceController() {
        this.placeService = Instance.PLACE_SERVICE;
    }

    /**
     * Creates a new instance of PlaceController.
     *
     * @param placeService method parameter
     */
    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    /**
     * Lists all places.
     *
     * @return HTTP 200 with all places
     */
    @GET
    @Path("/all")
    @PermitAll
    @Operation(
            summary = "List all places",
            description = "Returns all places with id, name, address, city, zip code and capacity."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "All places",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = ResponsePlaceDto.class))
                    )
            )
    })
    public Response getAllPlaces() {
        List<ResponsePlaceDto> places = placeService.getAllPlaces();
        return Response.ok(places).build();
    }
}
