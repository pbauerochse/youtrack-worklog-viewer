package de.pbauerochse.worklogviewer.domain.timerangeprovider;

import de.pbauerochse.worklogviewer.domain.ReportTimerange;
import de.pbauerochse.worklogviewer.report.TimeRange;

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
