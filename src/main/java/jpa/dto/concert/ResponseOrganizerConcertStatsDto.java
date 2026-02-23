package jpa.dto.concert;

import io.swagger.v3.oas.annotations.media.Schema;
import jpa.enums.StatsGranularity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Detailed organizer dashboard payload for frontend charts.
 */
@Schema(name = "OrganizerConcertStats")
public record ResponseOrganizerConcertStatsDto(
        Instant generatedAt,
        Period period,
        Overview overview,
        OverviewDelta overviewVsPreviousPeriod,
        List<BreakdownItem> statusBreakdown,
        List<BreakdownItem> cityBreakdown,
        List<BreakdownItem> placeBreakdown,
        List<TimelinePoint> timeline,
        Rankings rankings,
        List<ConcertItem> concerts
) {
    public record Period(
            Instant from,
            Instant to,
            StatsGranularity granularity,
            Integer top,
            Boolean includeConcerts
    ) {
    }

    public record Overview(
            Integer totalConcerts,
            Integer publishedConcerts,
            Integer pendingConcerts,
            Integer rejectedConcerts,
            Integer upcomingConcerts,
            Integer pastConcerts,
            Integer totalTicketQuantity,
            Integer totalTicketSold,
            Integer totalTicketAvailable,
            BigDecimal globalSellThroughRatePct,
            BigDecimal averageConcertSellThroughRatePct,
            BigDecimal grossRevenue,
            BigDecimal averageTicketPrice,
            BigDecimal averageRevenuePerConcert
    ) {
    }

    public record OverviewDelta(
            BigDecimal concertsDeltaPct,
            BigDecimal soldTicketsDeltaPct,
            BigDecimal revenueDeltaPct,
            BigDecimal sellThroughDeltaPct
    ) {
    }

    public record BreakdownItem(
            String key,
            String label,
            Integer concertCount,
            Integer ticketQuantity,
            Integer ticketSold,
            Integer ticketAvailable,
            BigDecimal sellThroughRatePct,
            BigDecimal grossRevenue,
            BigDecimal sharePct
    ) {
    }

    public record TimelinePoint(
            Instant bucketStart,
            Instant bucketEnd,
            String bucketLabel,
            Integer concertsCreated,
            Integer ticketQuantity,
            Integer ticketSold,
            BigDecimal revenueGross
    ) {
    }

    public record Rankings(
            List<RankingItem> topByRevenue,
            List<RankingItem> topBySellThrough,
            List<RankingItem> topByTicketsSold,
            List<RankingItem> worstBySellThrough
    ) {
    }

    public record RankingItem(
            UUID concertId,
            String concertTitle,
            String concertArtist,
            Instant concertDate,
            String concertStatus,
            Integer ticketQuantity,
            Integer ticketSold,
            BigDecimal sellThroughRatePct,
            BigDecimal grossRevenue
    ) {
    }

    public record ConcertItem(
            UUID concertId,
            String concertTitle,
            String concertArtist,
            Instant concertCreatedAt,
            Instant concertDate,
            String concertStatus,
            String placeName,
            String placeAddress,
            Integer placeZipCode,
            String placeCity,
            Integer placeCapacity,
            Integer ticketQuantity,
            Integer ticketSold,
            Integer ticketAvailable,
            BigDecimal sellThroughRatePct,
            BigDecimal grossRevenue,
            BigDecimal ticketUnitPrice,
            Boolean isPublished,
            Boolean isPast,
            Long daysUntilConcert
    ) {
    }
}
