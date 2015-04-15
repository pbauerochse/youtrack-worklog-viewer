package de.pbauerochse.youtrack.domain;

import de.pbauerochse.youtrack.domain.timerangeprovider.CurrentMonthTimerangeProvider;
import de.pbauerochse.youtrack.domain.timerangeprovider.CurrentWeekTimerangeProvider;
import de.pbauerochse.youtrack.domain.timerangeprovider.LastMonthTimerangeProvider;
import de.pbauerochse.youtrack.domain.timerangeprovider.LastWeekTimerangeProvider;

/**
 * @author Patrick Bauerochse
 * @since 13.04.15
 */
public enum ReportTimerange {

    THIS_WEEK("timerange.thisweek",   "THIS_WEEK",  new CurrentWeekTimerangeProvider()),
    LAST_WEEK("timerange.lastweek",   "LAST_WEEK",  new LastWeekTimerangeProvider()),
    THIS_MONTH("timerange.thismonth", "THIS_MONTH", new CurrentMonthTimerangeProvider()),
    LAST_MONTH("timerange.lastmonth", "LAST_MONTH", new LastMonthTimerangeProvider());

    private final String labelKey;
    private final String reportRange;
    private final TimerangeProvider timerangeProvider;

    ReportTimerange(String labelKey, String reportRange, TimerangeProvider timerangeProvider) {
        this.labelKey = labelKey;
        this.reportRange = reportRange;
        this.timerangeProvider = timerangeProvider;
    }

    public String getLabelKey() {
        return labelKey;
    }

    public String getReportRange() {
        return reportRange;
    }

    public TimerangeProvider getTimerangeProvider() {
        return timerangeProvider;
    }
}
