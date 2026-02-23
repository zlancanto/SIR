package jpa.config;

import jpa.enums.StatsGranularity;

public class DefaultOrganizerConcertStats {
    public static final String FROM = null;
    public static final String TO = null;
    public static final StatsGranularity GRANULARITY = StatsGranularity.MONTH;
    public static final Integer TOP = 10;
    public static final Boolean INCLUDE_CONCERTS = true;

    private DefaultOrganizerConcertStats() {}
}
