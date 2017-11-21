package de.pbauerochse.worklogviewer.youtrack.post2017;

import de.pbauerochse.worklogviewer.domain.ReportTimerange;

class NamedTimeRange {

    private final ReportTimerange reportTimerange;

    NamedTimeRange(ReportTimerange reportTimerange) {
        this.reportTimerange = reportTimerange;
    }

    public String getId() {
        return reportTimerange.getReportRange();
    }

}
