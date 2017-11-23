package de.pbauerochse.worklogviewer.youtrack.v20174.types;

import com.fasterxml.jackson.annotation.*;
import de.pbauerochse.worklogviewer.youtrack.domain.GroupByCategory;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkItemBasedGrouping implements Grouping, GroupByCategory {

    private final GroupingField field;

    @JsonCreator
    public WorkItemBasedGrouping(@JsonProperty("field") GroupingField field) {
        this.field = field;
    }

    @Override
    public String getId() {
        return field.getId();
    }

    @Override
    public String getPresentation() {
        return field.getPresentation();
    }

    @Override
    public GroupingField getField() {
        return field;
    }

    @JsonIgnore
    @Override
    public String getName() {
        return getPresentation();
    }

    @JsonIgnore
    @Override
    public boolean isValidYouTrackCategory() {
        return true;
    }
}
