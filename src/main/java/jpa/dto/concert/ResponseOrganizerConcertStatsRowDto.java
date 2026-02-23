package jpa.dto.concert;

import jpa.enums.ConcertStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Flat row projection used by DAO for organizer stats computation.
 */
public record ResponseOrganizerConcertStatsRowDto(
        UUID concertId,
        String concertTitle,
        String concertArtist,
        Instant concertCreatedAt,
        Instant concertDate,
        ConcertStatus concertStatus,
        String placeName,
        String placeAddress,
        Integer placeZipCode,
        String placeCity,
        Integer placeCapacity,
        Long ticketQuantity,
        Long ticketSold,
        BigDecimal ticketUnitPrice
) {
}
