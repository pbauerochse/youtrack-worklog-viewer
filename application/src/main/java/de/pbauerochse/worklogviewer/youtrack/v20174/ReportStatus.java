package de.pbauerochse.worklogviewer.youtrack.v20174;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportStatus {

    private final boolean calculationInProgress;
    private final int progress;

    @JsonCreator
    public ReportStatus(@JsonProperty("calculationInProgress") boolean calculationInProgress,
                        @JsonProperty("progress") int progress) {
        this.calculationInProgress = calculationInProgress;
        this.progress = progress;
    }

    public boolean isCalculationInProgress() {
        return calculationInProgress;
    }

    public int getProgress() {
        return progress;
    }
}
