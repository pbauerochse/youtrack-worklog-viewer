package de.pbauerochse.worklogviewer.youtrack.v20174.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupByTypes implements GroupingField {

    private final String id;
    private final String presentation;

    @JsonCreator
    public GroupByTypes(@JsonProperty("id") String id,
                        @JsonProperty("presentation") String presentation) {
        this.id = id;
        this.presentation = presentation;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getPresentation() {
        return presentation;
    }
}
