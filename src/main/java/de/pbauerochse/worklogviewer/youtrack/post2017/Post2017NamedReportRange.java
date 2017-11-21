package de.pbauerochse.worklogviewer.youtrack.post2017;

import de.pbauerochse.worklogviewer.domain.ReportTimerange;

public class Post2017NamedReportRange implements Post2017ReportRange {

    private final ReportTimerange reportTimerange;

    Post2017NamedReportRange(ReportTimerange reportTimerange) {
        this.reportTimerange = reportTimerange;
    }

    public NamedTimeRange getRange() {
        return new NamedTimeRange(reportTimerange);
    }

}
