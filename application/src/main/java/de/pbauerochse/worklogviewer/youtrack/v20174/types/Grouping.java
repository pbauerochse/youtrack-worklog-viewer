package de.pbauerochse.worklogviewer.youtrack.v20174.types;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "$type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = WorkItemBasedGrouping.class, name = "jetbrains.charisma.smartui.report.time.WorkItemBasedGrouping"),
        @JsonSubTypes.Type(value = FieldBasedGrouping.class, name = "jetbrains.charisma.smartui.report.time.FieldBasedGrouping")

})
@Deprecated
public interface Grouping {

    String getId();

    String getPresentation();

    GroupingField getField();

}
