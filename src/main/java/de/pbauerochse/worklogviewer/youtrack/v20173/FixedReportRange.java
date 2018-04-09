package de.pbauerochse.worklogviewer.youtrack.v20173;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

class FixedReportRange implements ReportRange {

    private static final ZoneId EXPECTED_TIMEZONE = ZoneId.of("UTC");

    /**
     * Report names must not container <, > or / so we can't use
     * the regular formatter (the US date format for example contains / )
     **/
    private static final DateTimeFormatter REPORT_NAME_FORMATTER = DateTimeFormatter.ofPattern("ddMM");

    private final long from;
    private final long to;
    private final String name;

    @JsonCreator
    public FixedReportRange(@JsonProperty("name") String name,
                            @JsonProperty("from") long from,
                            @JsonProperty("to") long to) {
        this.name = name;
        this.from = from;
        this.to = to;
    }

    FixedReportRange(LocalDate startDate, LocalDate endDate) {
        ZonedDateTime zonedStartDateTime = startDate.atStartOfDay(EXPECTED_TIMEZONE);
        ZonedDateTime zonedEndDateTime = endDate.plusDays(1).atStartOfDay(EXPECTED_TIMEZONE).minusNanos(1);

        from = zonedStartDateTime.toInstant().toEpochMilli();
        to = zonedEndDateTime.toInstant().toEpochMilli();
        name = REPORT_NAME_FORMATTER.format(startDate) + " - " + REPORT_NAME_FORMATTER.format(endDate);
    }

    public long getFrom() {
        return from;
    }

    public long getTo() {
        return to;
    }

    @Override
    public String toString() {
        return name;
    }
}
