package de.pbauerochse.worklogviewer.youtrack.v20173;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.pbauerochse.worklogviewer.youtrack.domain.GroupByCategory;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Grouping implements GroupByCategory {

    private final String id;
    private final String name;

    @JsonCreator
    public Grouping(@JsonProperty("id") String id, @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean isValidYouTrackCategory() {
        return true;
    }
}
