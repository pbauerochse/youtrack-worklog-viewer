package de.pbauerochse.worklogviewer.youtrack.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A category which can be used to group
 * tasks within the time report
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupByCategory {

    private final String id;
    private final String name;

    @JsonCreator
    public GroupByCategory(@JsonProperty("id") String id, @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
