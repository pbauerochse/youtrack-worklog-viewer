package de.pbauerochse.worklogviewer.youtrack.post2017;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.pbauerochse.worklogviewer.youtrack.ReportDetails;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Post2017ReportDetailsResponse implements ReportDetails {

    private final String id;
    private final Post2017ReportStatus status;

    @JsonCreator
    public Post2017ReportDetailsResponse(@JsonProperty("id") String id,
                                         @JsonProperty("status") Post2017ReportStatus status) {
        this.id = id;
        this.status = status;
    }

    @Override
    public String getReportId() {
        return id;
    }

    @Override
    public boolean isRecomputing() {
        return status.isCalculationInProgress();
    }

    @Override
    public boolean isReady() {
        return !isRecomputing() && status.getProgress() == -1;
    }
}
