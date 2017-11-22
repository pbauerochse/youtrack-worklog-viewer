package de.pbauerochse.worklogviewer.youtrack.v20174;

import de.pbauerochse.worklogviewer.domain.ReportTimerange;

public class NamedReportRange implements ReportRange {

    private final ReportTimerange reportTimerange;

    NamedReportRange(ReportTimerange reportTimerange) {
        this.reportTimerange = reportTimerange;
    }

    public NamedTimeRange getRange() {
        return new NamedTimeRange(reportTimerange);
    }

}
