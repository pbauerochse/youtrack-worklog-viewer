package de.pbauerochse.youtrack.connector.createreport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.pbauerochse.youtrack.connector.createreport.request.CreateReportRange;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrick Bauerochse
 * @since 14.04.15
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportParameters {

    private List<String> projects = new ArrayList<>(0);
    private String queryUrl;
    private CreateReportRange range;

    public List<String> getProjects() {
        return projects;
    }

    public void setProjects(List<String> projects) {
        this.projects = projects;
    }

    public String getQueryUrl() {
        return queryUrl;
    }

    public void setQueryUrl(String queryUrl) {
        this.queryUrl = queryUrl;
    }

    public CreateReportRange getRange() {
        return range;
    }

    public void setRange(CreateReportRange range) {
        this.range = range;
    }
}
