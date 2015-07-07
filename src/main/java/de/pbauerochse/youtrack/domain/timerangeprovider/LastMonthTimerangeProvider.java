package de.pbauerochse.youtrack.domain.timerangeprovider;

import de.pbauerochse.youtrack.domain.ReportTimerange;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

/**
 * @author Patrick Bauerochse
 * @since 13.04.15
 */
public class LastMonthTimerangeProvider extends BaseTimerangeProvider {

    LastMonthTimerangeProvider() {
        LocalDate now = LocalDate.now(ZoneId.systemDefault());
        LocalDate lastMonth = now.minus(1, ChronoUnit.MONTHS);
        startDate = lastMonth.withDayOfMonth(1);
        endDate = lastMonth.withDayOfMonth(lastMonth.getMonth().length(lastMonth.isLeapYear()));
    }

    @Override
    public ReportTimerange getReportTimerange() {
        return ReportTimerange.LAST_MONTH;
    }
}
