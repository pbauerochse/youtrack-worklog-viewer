package de.pbauerochse.youtrack.connector.createreport.request;

import de.pbauerochse.youtrack.connector.createreport.BasicReportDetails;
import de.pbauerochse.youtrack.domain.ReportTimerange;

/**
 * @author Patrick Bauerochse
 * @since 14.04.15
 */
public class CreateReportRequestEntity extends BasicReportDetails {

    private static final String REPORT_RANGE_TYPE_NAMED = "named";
    public static final String REPORT_TYPE_TIME = "time";

    public CreateReportRequestEntity(ReportTimerange timerange) {
        CreateReportRange range = new CreateReportRange();
        range.setName(timerange.getReportRange());
        range.setType(REPORT_RANGE_TYPE_NAMED);

        getParameters().setRange(range);
        setName("Timetracker: " + timerange.getReportRange());
        setType(REPORT_TYPE_TIME);
        setOwn(true);
    }
}

