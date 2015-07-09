package de.pbauerochse.worklogviewer.domain;

/**
 * @author Patrick Bauerochse
 * @since 13.04.15
 */
public enum ReportTimerange {

    THIS_WEEK("timerange.thisweek",   "THIS_WEEK"),
    LAST_WEEK("timerange.lastweek",   "LAST_WEEK"),
    THIS_MONTH("timerange.thismonth", "THIS_MONTH"),
    LAST_MONTH("timerange.lastmonth", "LAST_MONTH"),
    CUSTOM("timerange.custom", "LAST_MONTH");

    private final String labelKey;
    private final String reportRange;

    ReportTimerange(String labelKey, String reportRange) {
        this.labelKey = labelKey;
        this.reportRange = reportRange;
    }

    public String getLabelKey() {
        return labelKey;
    }

    public String getReportRange() {
        return reportRange;
    }
}
