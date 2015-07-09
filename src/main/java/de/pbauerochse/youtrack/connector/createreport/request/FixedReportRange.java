package de.pbauerochse.youtrack.connector.createreport.request;

import de.pbauerochse.youtrack.util.FormattingUtil;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * @author Patrick Bauerochse
 * @since 07.07.15
 */
public class FixedReportRange implements CreateReportRange {

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

        name = FormattingUtil.formatDate(startDate) + " - " + FormattingUtil.formatDate(endDate);
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
