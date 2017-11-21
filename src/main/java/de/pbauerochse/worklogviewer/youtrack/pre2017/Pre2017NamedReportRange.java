package de.pbauerochse.worklogviewer.youtrack.pre2017;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.pbauerochse.worklogviewer.domain.ReportTimerange;
import de.pbauerochse.worklogviewer.util.ExceptionUtil;

public class Pre2017NamedReportRange implements Pre2017ReportRange {

    private final String name;

    @JsonCreator
    public Pre2017NamedReportRange(@JsonProperty("name") String name) {
        this.name = name;
    }

    public Pre2017NamedReportRange(ReportTimerange timerange) {
        if (timerange == null || timerange == ReportTimerange.CUSTOM)
            throw ExceptionUtil.getIllegalArgumentException("exceptions.internal");
        this.name = timerange.getReportRange();
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
