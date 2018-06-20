package de.pbauerochse.worklogviewer.youtrack.v20174;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.pbauerochse.worklogviewer.youtrack.ReportDetails;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportDetailsResponse implements ReportDetails {

    private final String id;
    private final ReportStatus status;

    @JsonCreator
    ReportDetailsResponse(@JsonProperty("id") String id,
                          @JsonProperty("status") ReportStatus status) {
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
