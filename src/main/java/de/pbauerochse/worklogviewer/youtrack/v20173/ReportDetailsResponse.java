package de.pbauerochse.worklogviewer.youtrack.v20173;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.pbauerochse.worklogviewer.youtrack.ReportDetails;
import de.pbauerochse.worklogviewer.youtrack.createreport.BasicReportDetails;
import org.apache.commons.lang3.StringUtils;

/**
 * {"id":"116-37","name":"Timetracker: THIS_WEEK","ownerLogin":"bauerochse","type":"time","own":true,"visibleTo":null,"invalidationInterval":0,"state":"CALCULATING","lastCalculated":"—","progress":-1,"parameters":{"projects":[],"queryUrl":"/issues"}}
 *
 * @author Patrick Bauerochse
 * @since 15.04.15
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportDetailsResponse extends BasicReportDetails implements ReportDetails {

    private static final String READY_STATE = "READY";

    private String id;
    private String ownerLogin;
    private Long invalidationInterval;
    private String state;
    private String lastCalculated;
    private Long progress;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerLogin() {
        return ownerLogin;
    }

    public void setOwnerLogin(String ownerLogin) {
        this.ownerLogin = ownerLogin;
    }

    public Long getInvalidationInterval() {
        return invalidationInterval;
    }

    public void setInvalidationInterval(Long invalidationInterval) {
        this.invalidationInterval = invalidationInterval;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLastCalculated() {
        return lastCalculated;
    }

    public void setLastCalculated(String lastCalculated) {
        this.lastCalculated = lastCalculated;
    }

    public Long getProgress() {
        return progress;
    }

    public void setProgress(Long progress) {
        this.progress = progress;
    }

    @JsonIgnore
    @Override
    public String getReportId() {
        return getId();
    }

    @JsonIgnore
    @Override
    public boolean isRecomputing() {
        return !isReady();
    }

    @JsonIgnore
    @Override
    public boolean isReady() {
        return StringUtils.equals(READY_STATE, getState());
    }
}
