package de.pbauerochse.worklogviewer.domain.timerangeprovider;

import de.pbauerochse.worklogviewer.domain.ReportTimerange;
import de.pbauerochse.worklogviewer.report.TimeRange;

import java.time.LocalDate;

/**
 * TimerangeProvider for custom provided dates
 */
public class CustomTimerangeProvider extends BaseTimerangeProvider {

    private final TimeRange timeRange;

    CustomTimerangeProvider(LocalDate startDate, LocalDate endDate) {
        this.timeRange = new TimeRange(startDate, endDate);
    }

    @Override
    public TimeRange getTimeRange() {
        return timeRange;
    }

    @Override
    public ReportTimerange getReportTimerange() {
        return ReportTimerange.CUSTOM;
    }

}
