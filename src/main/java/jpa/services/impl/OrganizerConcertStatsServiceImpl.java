package jpa.services.impl;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import jpa.config.DefaultOrganizerConcertStats;
import jpa.dao.abstracts.OrganizerConcertStatsDao;
import jpa.dao.abstracts.UserDao;
import jpa.dto.concert.RequestOrganizerConcertStatsDto;
import jpa.dto.concert.ResponseOrganizerConcertStatsDto;
import jpa.dto.concert.ResponseOrganizerConcertStatsRowDto;
import jpa.entities.Organizer;
import jpa.entities.User;
import jpa.enums.ConcertStatus;
import jpa.enums.StatsGranularity;
import jpa.services.interfaces.OrganizerConcertStatsService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.function.Function;

import static jpa.utils.StringValidation.normalizeRequired;

/**
 * Service implementation dedicated to organizer concert statistics.
 */
public class OrganizerConcertStatsServiceImpl implements OrganizerConcertStatsService {
    private final OrganizerConcertStatsDao organizerConcertStatsDao;
    private final UserDao userDao;

    /**
     * Creates a service dedicated to organizer stats.
     *
     * @param organizerConcertStatsDao DAO used to query concert stats rows
     * @param userDao                  DAO used to resolve authenticated user
     */
    public OrganizerConcertStatsServiceImpl(
            OrganizerConcertStatsDao organizerConcertStatsDao,
            UserDao userDao
    ) {
        this.organizerConcertStatsDao = organizerConcertStatsDao;
        this.userDao = userDao;
    }

    /**
     * Executes getOrganizerConcertStats operation.
     *
     * @param authenticatedOrganizerEmail method parameter
     * @param request                     method parameter
     * @return operation result
     */
    @Override
    public ResponseOrganizerConcertStatsDto getOrganizerConcertStats(
            String authenticatedOrganizerEmail,
            RequestOrganizerConcertStatsDto request
    ) {
        String email = normalizeRequired("authenticatedOrganizerEmail", authenticatedOrganizerEmail)
                .toLowerCase(Locale.ROOT);

        User authenticatedUser = userDao.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Organizer not found"));
        if (!(authenticatedUser instanceof Organizer organizer)) {
            throw new ForbiddenException("User is not an organizer");
        }

        RequestOrganizerConcertStatsDto safeRequest = request == null
                ? new RequestOrganizerConcertStatsDto(
                DefaultOrganizerConcertStats.FROM,
                DefaultOrganizerConcertStats.TO,
                DefaultOrganizerConcertStats.GRANULARITY,
                DefaultOrganizerConcertStats.TOP,
                DefaultOrganizerConcertStats.INCLUDE_CONCERTS)
                : request;

        Instant from = parseInstantOrNull("from", safeRequest.from());
        Instant to = parseInstantOrNull("to", safeRequest.to());
        if (from != null && to != null && from.isAfter(to)) {
            throw new BadRequestException("from must be <= to");
        }

        StatsGranularity granularity = safeRequest.granularity() == null
                ? DefaultOrganizerConcertStats.GRANULARITY
                : safeRequest.granularity();

        int top = safeRequest.top() == null
                ? DefaultOrganizerConcertStats.TOP
                : safeRequest.top();

        if (top < 1 || top > 100) {
            throw new BadRequestException("top must be between 1 and 100");
        }

        boolean includeConcerts = safeRequest.includeConcerts() == null || safeRequest.includeConcerts();

        List<ResponseOrganizerConcertStatsRowDto> rows = organizerConcertStatsDao.findOrganizerConcertStatsRows(
                organizer.getId(),
                from,
                to
        );

        Instant now = Instant.now();
        List<ResponseOrganizerConcertStatsDto.ConcertItem> concertItems = rows.stream()
                .map(row -> toConcertItem(row, now))
                .toList();

        ResponseOrganizerConcertStatsDto.Overview overview = buildOverview(concertItems, now);
        List<ResponseOrganizerConcertStatsDto.BreakdownItem> statusBreakdown = buildBreakdown(
                concertItems,
                item -> item.concertStatus() == null ? "UNKNOWN_STATUS" : item.concertStatus(),
                item -> item.concertStatus() == null ? "Unknown status" : item.concertStatus()
        );
        List<ResponseOrganizerConcertStatsDto.BreakdownItem> cityBreakdown = buildBreakdown(
                concertItems,
                item -> item.placeCity() == null ? "UNKNOWN_CITY" : item.placeCity(),
                item -> item.placeCity() == null ? "Unknown city" : item.placeCity()
        );
        List<ResponseOrganizerConcertStatsDto.BreakdownItem> placeBreakdown = buildBreakdown(
                concertItems,
                item -> item.placeName() == null ? "UNKNOWN_PLACE" : item.placeName(),
                item -> item.placeName() == null ? "Unknown place" : item.placeName()
        );
        List<ResponseOrganizerConcertStatsDto.TimelinePoint> timeline = buildTimeline(concertItems, granularity);
        ResponseOrganizerConcertStatsDto.Rankings rankings = buildRankings(concertItems, top);

        ResponseOrganizerConcertStatsDto.OverviewDelta delta = buildDeltaIfPossible(
                organizer.getId(),
                from,
                to,
                overview,
                now
        );

        Instant periodFrom = resolvePeriodFrom(from, rows, now);
        Instant periodTo = resolvePeriodTo(to, rows, now);

        return new ResponseOrganizerConcertStatsDto(
                Instant.now(),
                new ResponseOrganizerConcertStatsDto.Period(periodFrom, periodTo, granularity, top, includeConcerts),
                overview,
                delta,
                statusBreakdown,
                cityBreakdown,
                placeBreakdown,
                timeline,
                rankings,
                includeConcerts ? concertItems : List.of()
        );
    }

    private ResponseOrganizerConcertStatsDto.ConcertItem toConcertItem(
            ResponseOrganizerConcertStatsRowDto row,
            Instant now
    ) {
        int quantity = safeInt(row.ticketQuantity());
        int sold = safeInt(row.ticketSold());
        int available = Math.max(0, quantity - sold);

        BigDecimal unitPrice = row.ticketUnitPrice() == null
                ? BigDecimal.ZERO
                : row.ticketUnitPrice().setScale(2, RoundingMode.HALF_UP);
        BigDecimal revenue = unitPrice.multiply(BigDecimal.valueOf(sold)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal sellThrough = percent(sold, quantity);

        Boolean isPublished = row.concertStatus() == ConcertStatus.PUBLISHED;
        Boolean isPast = row.concertDate() == null ? null : row.concertDate().isBefore(now);
        Long daysUntilConcert = row.concertDate() == null ? null : Duration.between(now, row.concertDate()).toDays();

        return new ResponseOrganizerConcertStatsDto.ConcertItem(
                row.concertId(),
                row.concertTitle(),
                row.concertArtist(),
                row.concertCreatedAt(),
                row.concertDate(),
                row.concertStatus() == null ? null : row.concertStatus().name(),
                row.placeName(),
                row.placeAddress(),
                row.placeZipCode(),
                row.placeCity(),
                row.placeCapacity(),
                quantity,
                sold,
                available,
                sellThrough,
                revenue,
                unitPrice,
                isPublished,
                isPast,
                daysUntilConcert
        );
    }

    private ResponseOrganizerConcertStatsDto.Overview buildOverview(
            List<ResponseOrganizerConcertStatsDto.ConcertItem> concerts,
            Instant now
    ) {
        int totalConcerts = concerts.size();
        int published = (int) concerts.stream().filter(c -> "PUBLISHED".equals(c.concertStatus())).count();
        int pending = (int) concerts.stream().filter(c -> "PENDING_VALIDATION".equals(c.concertStatus())).count();
        int rejected = (int) concerts.stream().filter(c -> "REJECTED".equals(c.concertStatus())).count();
        int upcoming = (int) concerts.stream()
                .filter(c -> c.concertDate() != null && c.concertDate().isAfter(now))
                .count();
        int past = (int) concerts.stream()
                .filter(c -> c.concertDate() != null && c.concertDate().isBefore(now))
                .count();

        int totalQty = concerts.stream().mapToInt(ResponseOrganizerConcertStatsDto.ConcertItem::ticketQuantity).sum();
        int totalSold = concerts.stream().mapToInt(ResponseOrganizerConcertStatsDto.ConcertItem::ticketSold).sum();
        int totalAvailable = Math.max(0, totalQty - totalSold);

        BigDecimal grossRevenue = concerts.stream()
                .map(ResponseOrganizerConcertStatsDto.ConcertItem::grossRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal globalSellThrough = percent(totalSold, totalQty);
        BigDecimal averageConcertSellThrough = average(
                concerts.stream().map(ResponseOrganizerConcertStatsDto.ConcertItem::sellThroughRatePct).toList()
        );

        BigDecimal averageTicketPrice = totalSold == 0
                ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : grossRevenue.divide(BigDecimal.valueOf(totalSold), 2, RoundingMode.HALF_UP);

        BigDecimal averageRevenuePerConcert = totalConcerts == 0
                ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : grossRevenue.divide(BigDecimal.valueOf(totalConcerts), 2, RoundingMode.HALF_UP);

        return new ResponseOrganizerConcertStatsDto.Overview(
                totalConcerts,
                published,
                pending,
                rejected,
                upcoming,
                past,
                totalQty,
                totalSold,
                totalAvailable,
                globalSellThrough,
                averageConcertSellThrough,
                grossRevenue,
                averageTicketPrice,
                averageRevenuePerConcert
        );
    }

    private ResponseOrganizerConcertStatsDto.OverviewDelta buildDeltaIfPossible(
            UUID organizerId,
            Instant from,
            Instant to,
            ResponseOrganizerConcertStatsDto.Overview current,
            Instant now
    ) {
        if (from == null || to == null || !to.isAfter(from)) {
            return new ResponseOrganizerConcertStatsDto.OverviewDelta(null, null, null, null);
        }

        Duration duration = Duration.between(from, to);
        Instant previousTo = from.minusSeconds(1);
        Instant previousFrom = previousTo.minus(duration);

        List<ResponseOrganizerConcertStatsRowDto> previousRows = organizerConcertStatsDao.findOrganizerConcertStatsRows(
                organizerId,
                previousFrom,
                previousTo
        );
        ResponseOrganizerConcertStatsDto.Overview previous = buildOverview(
                previousRows.stream().map(row -> toConcertItem(row, now)).toList(),
                now
        );

        return new ResponseOrganizerConcertStatsDto.OverviewDelta(
                deltaPct(current.totalConcerts(), previous.totalConcerts()),
                deltaPct(current.totalTicketSold(), previous.totalTicketSold()),
                deltaPct(current.grossRevenue(), previous.grossRevenue()),
                deltaPct(current.globalSellThroughRatePct(), previous.globalSellThroughRatePct())
        );
    }

    private List<ResponseOrganizerConcertStatsDto.BreakdownItem> buildBreakdown(
            List<ResponseOrganizerConcertStatsDto.ConcertItem> concerts,
            Function<ResponseOrganizerConcertStatsDto.ConcertItem, String> keyFn,
            Function<ResponseOrganizerConcertStatsDto.ConcertItem, String> labelFn
    ) {
        Map<String, BreakdownAccumulator> map = new LinkedHashMap<>();
        for (ResponseOrganizerConcertStatsDto.ConcertItem item : concerts) {
            String key = keyFn.apply(item);
            String label = labelFn.apply(item);
            map.computeIfAbsent(key, ignored -> new BreakdownAccumulator(label)).add(item);
        }

        int totalConcerts = concerts.size();

        return map.entrySet().stream()
                .map(entry -> {
                    BreakdownAccumulator acc = entry.getValue();
                    BigDecimal sharePct = percent(acc.concertCount, totalConcerts);
                    BigDecimal sellThrough = percent(acc.ticketSold, acc.ticketQuantity);
                    return new ResponseOrganizerConcertStatsDto.BreakdownItem(
                            entry.getKey(),
                            acc.label,
                            acc.concertCount,
                            acc.ticketQuantity,
                            acc.ticketSold,
                            Math.max(0, acc.ticketQuantity - acc.ticketSold),
                            sellThrough,
                            acc.grossRevenue.setScale(2, RoundingMode.HALF_UP),
                            sharePct
                    );
                })
                .sorted(Comparator.comparing(ResponseOrganizerConcertStatsDto.BreakdownItem::concertCount).reversed())
                .toList();
    }

    private List<ResponseOrganizerConcertStatsDto.TimelinePoint> buildTimeline(
            List<ResponseOrganizerConcertStatsDto.ConcertItem> concerts,
            StatsGranularity granularity
    ) {
        Map<Instant, TimelineAccumulator> buckets = new TreeMap<>();
        for (ResponseOrganizerConcertStatsDto.ConcertItem item : concerts) {
            Instant source = item.concertCreatedAt() != null ? item.concertCreatedAt() : item.concertDate();
            if (source == null) {
                continue;
            }
            Instant bucketStart = bucketStart(source, granularity);
            buckets.computeIfAbsent(bucketStart, ignored -> new TimelineAccumulator()).add(item);
        }

        return buckets.entrySet().stream()
                .map(entry -> {
                    Instant start = entry.getKey();
                    Instant end = bucketEnd(start, granularity);
                    String label = bucketLabel(start, granularity);
                    TimelineAccumulator acc = entry.getValue();
                    return new ResponseOrganizerConcertStatsDto.TimelinePoint(
                            start,
                            end,
                            label,
                            acc.concertsCreated,
                            acc.ticketQuantity,
                            acc.ticketSold,
                            acc.grossRevenue.setScale(2, RoundingMode.HALF_UP)
                    );
                })
                .toList();
    }

    private ResponseOrganizerConcertStatsDto.Rankings buildRankings(
            List<ResponseOrganizerConcertStatsDto.ConcertItem> concerts,
            int top
    ) {
        Comparator<ResponseOrganizerConcertStatsDto.ConcertItem> byRevenue =
                Comparator.comparing(ResponseOrganizerConcertStatsDto.ConcertItem::grossRevenue).reversed();
        Comparator<ResponseOrganizerConcertStatsDto.ConcertItem> bySellThrough =
                Comparator.comparing(ResponseOrganizerConcertStatsDto.ConcertItem::sellThroughRatePct).reversed();
        Comparator<ResponseOrganizerConcertStatsDto.ConcertItem> bySold =
                Comparator.comparing(ResponseOrganizerConcertStatsDto.ConcertItem::ticketSold).reversed();

        List<ResponseOrganizerConcertStatsDto.RankingItem> topByRevenue = concerts.stream()
                .sorted(byRevenue)
                .limit(top)
                .map(this::toRanking)
                .toList();
        List<ResponseOrganizerConcertStatsDto.RankingItem> topBySellThrough = concerts.stream()
                .sorted(bySellThrough)
                .limit(top)
                .map(this::toRanking)
                .toList();
        List<ResponseOrganizerConcertStatsDto.RankingItem> topByTicketsSold = concerts.stream()
                .sorted(bySold)
                .limit(top)
                .map(this::toRanking)
                .toList();
        List<ResponseOrganizerConcertStatsDto.RankingItem> worstBySellThrough = concerts.stream()
                .sorted(Comparator.comparing(ResponseOrganizerConcertStatsDto.ConcertItem::sellThroughRatePct))
                .limit(top)
                .map(this::toRanking)
                .toList();

        return new ResponseOrganizerConcertStatsDto.Rankings(
                topByRevenue,
                topBySellThrough,
                topByTicketsSold,
                worstBySellThrough
        );
    }

    private ResponseOrganizerConcertStatsDto.RankingItem toRanking(ResponseOrganizerConcertStatsDto.ConcertItem item) {
        return new ResponseOrganizerConcertStatsDto.RankingItem(
                item.concertId(),
                item.concertTitle(),
                item.concertArtist(),
                item.concertDate(),
                item.concertStatus(),
                item.ticketQuantity(),
                item.ticketSold(),
                item.sellThroughRatePct(),
                item.grossRevenue()
        );
    }

    private Instant parseInstantOrNull(String field, String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }

        try {
            return Instant.parse(raw.trim());
        } catch (DateTimeParseException ex) {
            throw new BadRequestException(
                    field + " must be ISO-8601 instant (example: 2026-01-01T00:00:00Z)"
            );
        }
    }

    private int safeInt(Long value) {
        return value == null ? 0 : Math.toIntExact(value);
    }

    private BigDecimal average(List<BigDecimal> values) {
        if (values == null || values.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal sum = values.stream()
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sum.divide(BigDecimal.valueOf(values.size()), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal percent(int part, int total) {
        if (total <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return BigDecimal.valueOf(part)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal deltaPct(int current, int previous) {
        if (previous <= 0) {
            return null;
        }

        return BigDecimal.valueOf(current - previous)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(previous), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal deltaPct(BigDecimal current, BigDecimal previous) {
        if (current == null || previous == null || previous.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        return current.subtract(previous)
                .multiply(BigDecimal.valueOf(100))
                .divide(previous, 2, RoundingMode.HALF_UP);
    }

    private Instant bucketStart(Instant source, StatsGranularity granularity) {
        ZonedDateTime zdt = source.atZone(ZoneOffset.UTC);
        return switch (granularity) {
            case DAY -> zdt.toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant();
            case WEEK -> zdt.toLocalDate()
                    .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                    .atStartOfDay(ZoneOffset.UTC)
                    .toInstant();
            case MONTH -> zdt.withDayOfMonth(1).toLocalDate().atStartOfDay(ZoneOffset.UTC).toInstant();
        };
    }

    private Instant bucketEnd(Instant start, StatsGranularity granularity) {
        ZonedDateTime zdt = start.atZone(ZoneOffset.UTC);
        return switch (granularity) {
            case DAY -> zdt.plusDays(1).toInstant();
            case WEEK -> zdt.plusWeeks(1).toInstant();
            case MONTH -> zdt.plusMonths(1).toInstant();
        };
    }

    private String bucketLabel(Instant start, StatsGranularity granularity) {
        ZonedDateTime zdt = start.atZone(ZoneOffset.UTC);
        return switch (granularity) {
            case DAY -> zdt.toLocalDate().toString();
            case WEEK -> {
                WeekFields weekFields = WeekFields.ISO;
                int week = zdt.get(weekFields.weekOfWeekBasedYear());
                int year = zdt.get(weekFields.weekBasedYear());
                yield year + "-W" + String.format("%02d", week);
            }
            case MONTH -> String.format("%d-%02d", zdt.getYear(), zdt.getMonthValue());
        };
    }

    private Instant resolvePeriodFrom(
            Instant from,
            List<ResponseOrganizerConcertStatsRowDto> rows,
            Instant now
    ) {
        if (from != null) {
            return from;
        }

        return rows.stream()
                .map(ResponseOrganizerConcertStatsRowDto::concertDate)
                .filter(Objects::nonNull)
                .min(Instant::compareTo)
                .orElse(now);
    }

    private Instant resolvePeriodTo(
            Instant to,
            List<ResponseOrganizerConcertStatsRowDto> rows,
            Instant now
    ) {
        if (to != null) {
            return to;
        }

        return rows.stream()
                .map(ResponseOrganizerConcertStatsRowDto::concertDate)
                .filter(Objects::nonNull)
                .max(Instant::compareTo)
                .orElse(now);
    }

    private static final class BreakdownAccumulator {
        private final String label;
        private int concertCount;
        private int ticketQuantity;
        private int ticketSold;
        private BigDecimal grossRevenue = BigDecimal.ZERO;

        private BreakdownAccumulator(String label) {
            this.label = label;
        }

        private void add(ResponseOrganizerConcertStatsDto.ConcertItem item) {
            concertCount += 1;
            ticketQuantity += item.ticketQuantity();
            ticketSold += item.ticketSold();
            grossRevenue = grossRevenue.add(item.grossRevenue());
        }
    }

    private static final class TimelineAccumulator {
        private int concertsCreated;
        private int ticketQuantity;
        private int ticketSold;
        private BigDecimal grossRevenue = BigDecimal.ZERO;

        private void add(ResponseOrganizerConcertStatsDto.ConcertItem item) {
            concertsCreated += 1;
            ticketQuantity += item.ticketQuantity();
            ticketSold += item.ticketSold();
            grossRevenue = grossRevenue.add(item.grossRevenue());
        }
    }
}
