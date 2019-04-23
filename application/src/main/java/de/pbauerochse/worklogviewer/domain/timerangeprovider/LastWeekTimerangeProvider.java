package de.pbauerochse.worklogviewer.domain.timerangeprovider;

import de.pbauerochse.worklogviewer.domain.ReportTimerange;
import de.pbauerochse.worklogviewer.report.TimeRange;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

/**
 * @author Patrick Bauerochse
 * @since 13.04.15
 */
public class LastWeekTimerangeProvider extends BaseTimerangeProvider {

    @Override
    public TimeRange getTimeRange() {
        return TimeRange.lastWeek();
    }

    @Override
    public ReportTimerange getReportTimerange() {
        return ReportTimerange.LAST_WEEK;
    }
}
