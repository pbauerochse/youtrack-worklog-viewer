package de.pbauerochse.youtrack.connector.createreport.request;

import de.pbauerochse.youtrack.connector.createreport.BasicReportDetails;
import de.pbauerochse.youtrack.domain.GroupByCategory;
import de.pbauerochse.youtrack.domain.ReportTimerange;
import de.pbauerochse.youtrack.domain.TimerangeProvider;
import de.pbauerochse.youtrack.fx.tasks.FetchTimereportContext;

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

        GroupByCategory groupByCategory = fetchTimereportContext.getGroupByCategory();
        if (groupByCategory != null && !groupByCategory.isNoGroupByCriteria()) {
            getParameters().setGroupById(groupByCategory.getId());
        }
    }

    private void init(CreateReportRange range) {
        getParameters().setRange(range);
        setName("Timetracker: " + range.toString());
        setType(REPORT_TYPE_TIME);
        setOwn(true);
    }
}

