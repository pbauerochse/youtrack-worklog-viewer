package de.pbauerochse.worklogviewer.domain.timerangeprovider;

import de.pbauerochse.worklogviewer.domain.ReportTimerange;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * @author Patrick Bauerochse
 * @since 13.04.15
 */
public class CurrentMonthTimerangeProvider extends BaseTimerangeProvider {

    CurrentMonthTimerangeProvider() {
        LocalDate now = LocalDate.now(ZoneId.systemDefault());
        startDate = now.withDayOfMonth(1);
        endDate = now.withDayOfMonth(now.getMonth().length(now.isLeapYear()));
    }

    @Override
    public ReportTimerange getReportTimerange() {
        return ReportTimerange.THIS_MONTH;
    }

}
