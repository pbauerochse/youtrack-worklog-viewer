package de.pbauerochse.worklogviewer.youtrack.v20174.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomFilterField implements GroupingField {

    private final CustomField customField;
    private final String presentation;
    private final List<Project> projects;
    private final String id;
    private final boolean aggregateable;
    private final boolean sortable;
    private final String name;

    @JsonCreator
    public CustomFilterField(@JsonProperty("customField") CustomField customField,
                             @JsonProperty("presentation") String presentation,
                             @JsonProperty("projects") List<Project> projects,
                             @JsonProperty("id") String id,
                             @JsonProperty("aggregateable") boolean aggregateable,
                             @JsonProperty("sortable") boolean sortable,
                             @JsonProperty("name") String name) {
        this.customField = customField;
        this.presentation = presentation;
        this.projects = projects;
        this.id = id;
        this.aggregateable = aggregateable;
        this.sortable = sortable;
        this.name = name;
    }

    public CustomField getCustomField() {
        return customField;
    }

    @Override
    public String getPresentation() {
        return presentation;
    }

    public List<Project> getProjects() {
        return projects;
    }

    @Override
    public String getId() {
        return id;
    }

    public boolean isAggregateable() {
        return aggregateable;
    }

    public boolean isSortable() {
        return sortable;
    }

    public String getName() {
        return name;
    }
}
