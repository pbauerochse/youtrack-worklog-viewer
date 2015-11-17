package de.pbauerochse.worklogviewer.youtrack.createreport.request;

import de.pbauerochse.worklogviewer.util.FormattingUtil;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Patrick Bauerochse
 * @since 07.07.15
 */
public class FixedReportRange implements CreateReportRange {

    /**
     * Report names must not container <, > or / so we can't use
     * the regular formatter (the US date format for example contains / )
     **/
    private static final DateTimeFormatter REPORT_NAME_FORMATTER = DateTimeFormatter.ofPattern("ddMM");

    private long from;

    private long to;
    private transient String name;

    public FixedReportRange() {}
    public FixedReportRange(LocalDate startDate, LocalDate endDate) {
        ZoneId defaultZone = ZoneId.systemDefault();
        ZonedDateTime zonedStartDateTime = startDate.atStartOfDay(defaultZone);
        ZonedDateTime zonedEndDateTime = endDate.atStartOfDay(defaultZone);

        from = zonedStartDateTime.toInstant().toEpochMilli();
        to = zonedEndDateTime.toInstant().toEpochMilli();

        name = REPORT_NAME_FORMATTER.format(startDate) + " - " + REPORT_NAME_FORMATTER.format(endDate);
    }

    public long getFrom() {
        return from;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public long getTo() {
        return to;
    }

    public void setTo(long to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return name;
    }
}
