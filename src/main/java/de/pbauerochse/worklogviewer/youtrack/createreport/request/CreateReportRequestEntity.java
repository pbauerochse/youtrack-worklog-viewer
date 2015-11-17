package de.pbauerochse.worklogviewer.youtrack.createreport.request;

import de.pbauerochse.worklogviewer.domain.ReportTimerange;
import de.pbauerochse.worklogviewer.domain.TimerangeProvider;
import de.pbauerochse.worklogviewer.fx.tasks.FetchTimereportContext;
import de.pbauerochse.worklogviewer.youtrack.createreport.BasicReportDetails;

/**
 * @author Patrick Bauerochse
 * @since 14.04.15
 */
public class CreateReportRequestEntity extends BasicReportDetails {

    private static final String REPORT_TYPE_TIME = "time";

    public CreateReportRequestEntity(FetchTimereportContext fetchTimereportContext) {
        TimerangeProvider timerangeProvider = fetchTimereportContext.getTimerangeProvider();
        if (timerangeProvider.getReportTimerange() == ReportTimerange.CUSTOM) {
            init(new FixedReportRange(timerangeProvider.getStartDate(), timerangeProvider.getEndDate()));
        } else {
            init(new NamedReportRange(timerangeProvider.getReportTimerange()));
        }

        fetchTimereportContext.getGroupByCategory().ifPresent(groupByCategory -> getParameters().setGroupById(groupByCategory.getId()));
    }

    private void init(CreateReportRange range) {
        getParameters().setRange(range);
        setName("Timetracker: " + range.toString());
        setType(REPORT_TYPE_TIME);
        setOwn(true);
    }
}

