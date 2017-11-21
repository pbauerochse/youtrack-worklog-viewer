package de.pbauerochse.worklogviewer.youtrack.pre2017;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import de.pbauerochse.worklogviewer.domain.ReportTimerange;
import de.pbauerochse.worklogviewer.domain.TimerangeProvider;
import de.pbauerochse.worklogviewer.youtrack.TimereportContext;
import de.pbauerochse.worklogviewer.youtrack.createreport.ReportParameters;
import de.pbauerochse.worklogviewer.youtrack.domain.GroupByCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
class Pre2017CreateReportRequestPayload {

    private static final Logger LOGGER = LoggerFactory.getLogger(Pre2017CreateReportRequestPayload.class);

    private static final String TIMEREPORT_TYPE = "time";
    private final TimereportContext timereportContext;

    Pre2017CreateReportRequestPayload(TimereportContext timereportContext) {
        this.timereportContext = timereportContext;
    }

    public String getName() {
        TimerangeProvider timerangeProvider = timereportContext.getTimerangeProvider();
        ReportTimerange reportTimerange = timerangeProvider.getReportTimerange();
        return String.format("YT-WorklogViewer: %s", reportTimerange.name());
    }

    public String getType() {
        return TIMEREPORT_TYPE;
    }

    public boolean isOwn() {
        return true;
    }

    public ReportParameters getParameters() {
        ReportParameters parameters = new ReportParameters();

        Pre2017ReportRange reportRange = getReportRange();
        LOGGER.debug("Using timerange {}", reportRange);
        parameters.setRange(reportRange);

        timereportContext.getGroupByCategory()
                .map(GroupByCategory::getId)
                .ifPresent(parameters::setGroupById);

        return parameters;
    }

    @JsonIgnore
    private Pre2017ReportRange getReportRange() {
        TimerangeProvider timerangeProvider = timereportContext.getTimerangeProvider();
        ReportTimerange reportTimerange = timerangeProvider.getReportTimerange();

        if (reportTimerange == ReportTimerange.CUSTOM) {
            return new Pre2017FixedReportRange(timerangeProvider.getStartDate(), timerangeProvider.getEndDate());
        }

        return new Pre2017NamedReportRange(reportTimerange);
    }
}