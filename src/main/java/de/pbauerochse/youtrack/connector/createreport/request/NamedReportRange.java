package de.pbauerochse.youtrack.connector.createreport.request;

import de.pbauerochse.youtrack.domain.ReportTimerange;
import de.pbauerochse.youtrack.util.ExceptionUtil;

/**
 * @author Patrick Bauerochse
 * @since 07.07.15
 */
public class NamedReportRange implements CreateReportRange {

    private String name;

    public NamedReportRange() {}
    public NamedReportRange(ReportTimerange timerange) {
        if (timerange == null || timerange == ReportTimerange.CUSTOM) throw ExceptionUtil.getIllegalArgumentException("exceptions.internal");
        this.name = timerange.getReportRange();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
