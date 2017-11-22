package de.pbauerochse.worklogviewer.youtrack.v20173;

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
class CreateReportParameters {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateReportParameters.class);

    private static final String TIMEREPORT_TYPE = "time";
    private final TimereportContext timereportContext;

    CreateReportParameters(TimereportContext timereportContext) {
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

        ReportRange reportRange = getReportRange();
        LOGGER.debug("Using timerange {}", reportRange);
        parameters.setRange(reportRange);

        timereportContext.getGroupByCategory()
                .map(GroupByCategory::getId)
                .ifPresent(parameters::setGroupById);

        return parameters;
    }

    @JsonIgnore
    private ReportRange getReportRange() {
        TimerangeProvider timerangeProvider = timereportContext.getTimerangeProvider();
        ReportTimerange reportTimerange = timerangeProvider.getReportTimerange();

        if (reportTimerange == ReportTimerange.CUSTOM) {
            return new FixedReportRange(timerangeProvider.getStartDate(), timerangeProvider.getEndDate());
        }

        return new NamedReportRange(reportTimerange);
    }
}