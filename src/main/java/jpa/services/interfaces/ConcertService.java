package jpa.services.interfaces;

import jpa.dto.concert.CreateConcertRequestDto;
import jpa.dto.concert.ResponseConcertDetailsDto;
import jpa.dto.concert.ValidateConcertRequestDto;

import java.util.List;
import java.util.UUID;

/**
 * Service contract for ConcertService.
 */
public interface ConcertService {
    /**
     * Executes createEvent operation.
     *
     * @param request method parameter
     * @return operation result
     */
    ResponseConcertDetailsDto createConcert(CreateConcertRequestDto request);

    /**
     * Executes validateEvent operation.
     *
     * @param concertId method parameter
     * @param request method parameter
     * @param adminActionKey method parameter
     * @return operation result
     */
    ResponseConcertDetailsDto validateConcert(UUID concertId, ValidateConcertRequestDto request, String adminActionKey);

    /**
     * Executes getPublicEvents operation.
     *
     * @return operation result
     */
    List<ResponseConcertDetailsDto> getPublicConcerts();

    /**
     * Executes getPendingEvents operation.
     *
     * @param adminActionKey method parameter
     * @return operation result
     */
    List<ResponseConcertDetailsDto> getPendingConcerts(String adminActionKey);
}
