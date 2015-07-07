package de.pbauerochse.youtrack.domain.timerangeprovider;

import de.pbauerochse.youtrack.domain.ReportTimerange;

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

    LastWeekTimerangeProvider() {
        LocalDate now = LocalDate.now(ZoneId.systemDefault());
        LocalDate lastWeek = now.minus(1, ChronoUnit.WEEKS);

        startDate = lastWeek.with(ChronoField.DAY_OF_WEEK, DayOfWeek.MONDAY.getValue());
        endDate = lastWeek.with(ChronoField.DAY_OF_WEEK, DayOfWeek.SUNDAY.getValue());
    }

    @Override
    public ReportTimerange getReportTimerange() {
        return ReportTimerange.LAST_WEEK;
    }
}
