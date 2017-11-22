package de.pbauerochse.worklogviewer.youtrack.v20174;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.pbauerochse.worklogviewer.youtrack.domain.GroupByCategory;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Grouping {

    private final GroupByCategory groupByCategory;

    public Grouping(GroupByCategory groupByCategory) {
        this.groupByCategory = groupByCategory;
    }

    public String getId() {
        return groupByCategory.getId();
    }

    @JsonProperty("$type")
    public String getTypeHeader() {
        return "jetbrains.charisma.smartui.report.time.WorkItemBasedGrouping";
    }
}
