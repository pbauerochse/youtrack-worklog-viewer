package de.pbauerochse.worklogviewer.youtrack.post2017;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.pbauerochse.worklogviewer.domain.ReportTimerange;
import de.pbauerochse.worklogviewer.domain.TimerangeProvider;
import de.pbauerochse.worklogviewer.youtrack.TimereportContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
@JsonInclude(JsonInclude.Include.NON_NULL)
class Post2017CreateReportRequestPayload {

    private static final Logger LOGGER = LoggerFactory.getLogger(Post2017CreateReportRequestPayload.class);

    private final TimereportContext timereportContext;

    Post2017CreateReportRequestPayload(TimereportContext timereportContext) {
        this.timereportContext = timereportContext;
    }

    public String getName() {
        TimerangeProvider timerangeProvider = timereportContext.getTimerangeProvider();
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

    public Post2017ReportRange getRange() {
        TimerangeProvider timerangeProvider = timereportContext.getTimerangeProvider();
        ReportTimerange reportTimerange = timerangeProvider.getReportTimerange();

        if (reportTimerange == ReportTimerange.CUSTOM) {
            return new FixedTimeRange(timerangeProvider.getStartDate(), timerangeProvider.getEndDate());
        }

        return new Post2017NamedReportRange(reportTimerange);
    }

    public Grouping getGrouping() {
        return timereportContext.getGroupByCategory()
                .map(Grouping::new)
                .orElse(null);
    }
}