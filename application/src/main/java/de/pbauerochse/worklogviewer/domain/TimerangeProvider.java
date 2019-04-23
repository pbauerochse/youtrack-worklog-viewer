package de.pbauerochse.worklogviewer.domain;

import de.pbauerochse.worklogviewer.report.TimeRange;

/**
 * Provides the start- and enddate
 * for a given timerange
 */
public interface TimerangeProvider {
    TimeRange getTimeRange();
    ReportTimerange getReportTimerange();
}
