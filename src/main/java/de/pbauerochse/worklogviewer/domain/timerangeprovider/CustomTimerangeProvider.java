package de.pbauerochse.worklogviewer.domain.timerangeprovider;

import de.pbauerochse.worklogviewer.domain.ReportTimerange;

import java.time.LocalDate;

/**
 * TimerangeProvider for custom provided dates
 */
public class CustomTimerangeProvider extends BaseTimerangeProvider {

    CustomTimerangeProvider(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public ReportTimerange getReportTimerange() {
        return ReportTimerange.CUSTOM;
    }
}
