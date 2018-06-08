package de.pbauerochse.worklogviewer.youtrack.v20174;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.pbauerochse.worklogviewer.domain.ReportTimerange;
import de.pbauerochse.worklogviewer.domain.TimerangeProvider;
import de.pbauerochse.worklogviewer.youtrack.TimeReportParameters;
import de.pbauerochse.worklogviewer.youtrack.v20174.types.Grouping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
class CreateReportParameters {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateReportParameters.class);

    private final TimeReportParameters timeReportParameters;

    CreateReportParameters(TimeReportParameters timeReportParameters) {
        this.timeReportParameters = timeReportParameters;
    }

    public String getName() {
        TimerangeProvider timerangeProvider = timeReportParameters.getTimerangeProvider();
        ReportTimerange reportTimerange = timerangeProvider.getReportTimerange();
        return String.format("YT-WorklogViewer: %s", reportTimerange.name());
    }

    @JsonProperty("$type")
    public String getTypeHeader() {
        return "jetbrains.charisma.smartui.report.time.TimeReport";
    }

    public String getType() {
        return "time";
    }

    public boolean isOwn() {
        return true;
    }

    public ReportRange getRange() {
        TimerangeProvider timerangeProvider = timeReportParameters.getTimerangeProvider();
        ReportTimerange reportTimerange = timerangeProvider.getReportTimerange();

        if (reportTimerange == ReportTimerange.CUSTOM) {
            return new FixedTimeRange(timerangeProvider.getStartDate(), timerangeProvider.getEndDate());
        }

        return new NamedReportRange(reportTimerange);
    }

    public Grouping getGrouping() {
        return null;
//        return timeReportParameters.getGroupByCategory()
//                .filter(GroupByCategory::isValidYouTrackCategory)
//                .map(Grouping.class::cast)
//                .orElse(null);
    }
}