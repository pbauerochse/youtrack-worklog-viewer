package de.pbauerochse.worklogviewer.youtrack.post2017;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Post2017ReportStatus {

    private final boolean calculationInProgress;
    private final int progress;

    @JsonCreator
    public Post2017ReportStatus(@JsonProperty("calculationInProgress") boolean calculationInProgress,
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
