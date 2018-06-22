package de.pbauerochse.worklogviewer.youtrack.issuedetails;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by patrick on 31.10.15.
 * "field":[{"name":"resolved","value":"1446292986266"}]
 */
@Deprecated
@JsonIgnoreProperties(ignoreUnknown = true)
public class IssueField {

    private final String name;
    private final String value;

    @JsonCreator
    public IssueField(@JsonProperty("name") String name,
                      @JsonProperty("value") String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

}
