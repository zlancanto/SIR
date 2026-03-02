package jpa.services.interfaces;

import jpa.dto.place.ResponsePlaceDto;

import java.util.List;

/**
 * Service contract for PlaceService.
 */
public interface PlaceService {

    /**
     * Returns all places as lightweight projection.
     *
     * @return place projections
     */
    List<ResponsePlaceDto> getAllPlaces();
}
