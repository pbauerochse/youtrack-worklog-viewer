package de.pbauerochse.worklogviewer.youtrack.post2017;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "$type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = FixedTimeRange.class, name = "jetbrains.charisma.smartui.report.common.timeRange.FixedTimeRange"),
        @JsonSubTypes.Type(value = Post2017NamedReportRange.class, name = "jetbrains.charisma.smartui.report.common.timeRange.NamedTimeRange")
})
public interface Post2017ReportRange {
}
