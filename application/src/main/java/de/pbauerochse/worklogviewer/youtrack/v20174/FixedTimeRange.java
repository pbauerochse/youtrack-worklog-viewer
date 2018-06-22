package de.pbauerochse.worklogviewer.youtrack.v20174;

import java.time.LocalDate;
import java.time.ZoneId;
@Deprecated
public class FixedTimeRange implements ReportRange {

    private static final ZoneId EXPECTED_TIMEZONE = ZoneId.of("UTC");

    private final LocalDate startDate;
    private final LocalDate endDate;

    FixedTimeRange(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public long getFrom() {
        return startDate.atStartOfDay(EXPECTED_TIMEZONE).toInstant().toEpochMilli();
    }

    public long getTo() {
        return endDate.plusDays(1).atStartOfDay(EXPECTED_TIMEZONE).minusNanos(1).toInstant().toEpochMilli();
    }
}
