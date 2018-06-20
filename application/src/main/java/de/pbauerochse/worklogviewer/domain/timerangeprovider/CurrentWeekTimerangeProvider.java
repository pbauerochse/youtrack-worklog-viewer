package de.pbauerochse.worklogviewer.domain.timerangeprovider;

import de.pbauerochse.worklogviewer.domain.ReportTimerange;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoField;

/**
 * @author Patrick Bauerochse
 * @since 13.04.15
 */
public class CurrentWeekTimerangeProvider extends BaseTimerangeProvider {

    CurrentWeekTimerangeProvider() {
        LocalDate now = LocalDate.now(ZoneId.systemDefault());

        startDate = now.with(ChronoField.DAY_OF_WEEK, DayOfWeek.MONDAY.getValue());
        endDate = now.with(ChronoField.DAY_OF_WEEK, DayOfWeek.SUNDAY.getValue());
    }

    @Override
    public ReportTimerange getReportTimerange() {
        return ReportTimerange.THIS_WEEK;
    }
}