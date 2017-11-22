package de.pbauerochse.worklogviewer.youtrack.v20174;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "$type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = FixedTimeRange.class, name = "jetbrains.charisma.smartui.report.common.timeRange.FixedTimeRange"),
        @JsonSubTypes.Type(value = NamedReportRange.class, name = "jetbrains.charisma.smartui.report.common.timeRange.NamedTimeRange")
})
public interface ReportRange {
}
