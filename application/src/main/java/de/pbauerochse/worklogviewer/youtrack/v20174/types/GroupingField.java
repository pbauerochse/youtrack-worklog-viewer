package de.pbauerochse.worklogviewer.youtrack.v20174.types;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "$type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = GroupByTypes.class, name = "jetbrains.charisma.smartui.report.time.GroupByTypes"),
        @JsonSubTypes.Type(value = PredefinedFilterField.class, name = "jetbrains.charisma.keyword.PredefinedFilterField"),
        @JsonSubTypes.Type(value = CustomFilterField.class, name = "jetbrains.charisma.keyword.CustomFilterField"),
})
public interface GroupingField {

    String getId();

    String getPresentation();

}
