package de.pbauerochse.worklogviewer.youtrack.v20174.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PredefinedFilterField implements GroupingField {

    private final String id;
    private final String presentation;
    private final String name;
    private final boolean aggregateable;
    private final boolean sortable;

    @JsonCreator
    public PredefinedFilterField(@JsonProperty("id") String id,
                                 @JsonProperty("presentation") String presentation,
                                 @JsonProperty("name") String name,
                                 @JsonProperty("aggregateable") boolean aggregateable,
                                 @JsonProperty("sortable") boolean sortable) {
        this.id = id;
        this.presentation = presentation;
        this.name = name;
        this.aggregateable = aggregateable;
        this.sortable = sortable;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getPresentation() {
        return presentation;
    }

    public String getName() {
        return name;
    }

    public boolean isAggregateable() {
        return aggregateable;
    }

    public boolean isSortable() {
        return sortable;
    }
}
