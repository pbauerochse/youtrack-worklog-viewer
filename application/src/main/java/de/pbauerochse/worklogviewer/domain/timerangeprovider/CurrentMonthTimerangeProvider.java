package de.pbauerochse.worklogviewer.domain.timerangeprovider;

import de.pbauerochse.worklogviewer.domain.ReportTimerange;
import de.pbauerochse.worklogviewer.report.TimeRange;

/**
 * @author Patrick Bauerochse
 * @since 13.04.15
 */
public class CurrentMonthTimerangeProvider extends BaseTimerangeProvider {

    @Override
    public TimeRange getTimeRange() {
        return TimeRange.currentMonth();
    }

    @Override
    public ReportTimerange getReportTimerange() {
        return ReportTimerange.THIS_MONTH;
    }

}
