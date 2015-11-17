package de.pbauerochse.worklogviewer.youtrack.createreport.request;

import de.pbauerochse.worklogviewer.domain.ReportTimerange;
import de.pbauerochse.worklogviewer.domain.TimerangeProvider;
import de.pbauerochse.worklogviewer.fx.tasks.FetchTimereportContext;
import de.pbauerochse.worklogviewer.youtrack.createreport.BasicReportDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Patrick Bauerochse
 * @since 14.04.15
 */
public class CreateReportRequestEntity extends BasicReportDetails {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateReportRequestEntity.class);

    private static final String REPORT_TYPE_TIME = "time";

    public CreateReportRequestEntity(FetchTimereportContext fetchTimereportContext) {
        TimerangeProvider timerangeProvider = fetchTimereportContext.getTimerangeProvider();
        if (timerangeProvider.getReportTimerange() == ReportTimerange.CUSTOM) {
            LOGGER.debug("Using custom timerange {} - {}", timerangeProvider.getStartDate(), timerangeProvider.getEndDate());
            init(new FixedReportRange(timerangeProvider.getStartDate(), timerangeProvider.getEndDate()));
        } else {
            LOGGER.debug("Using named timerange {}", timerangeProvider.getReportTimerange());
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

