package de.pbauerochse.worklogviewer.youtrack.issuedetails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by patrick on 31.10.15.
 * "field":[{"name":"resolved","value":"1446292986266"}]
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IssueField {

    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
