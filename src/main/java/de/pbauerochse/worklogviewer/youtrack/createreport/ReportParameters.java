package de.pbauerochse.worklogviewer.youtrack.createreport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.pbauerochse.worklogviewer.youtrack.v20173.ReportRange;

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
    private ReportRange range;

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private String groupById;

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

    public ReportRange getRange() {
        return range;
    }

    public void setRange(ReportRange range) {
        this.range = range;
    }

    public String getGroupById() {
        return groupById;
    }

    public void setGroupById(String groupById) {
        this.groupById = groupById;
    }
}
