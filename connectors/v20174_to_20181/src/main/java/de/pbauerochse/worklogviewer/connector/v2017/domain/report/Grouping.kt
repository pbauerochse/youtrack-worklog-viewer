package de.pbauerochse.worklogviewer.connector.v2017.domain.report

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import de.pbauerochse.worklogviewer.connector.GroupByParameter

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "\$type", defaultImpl = UnknownGrouping::class)
@JsonSubTypes(
    JsonSubTypes.Type(value = WorkItemBasedGrouping::class, name = "jetbrains.charisma.smartui.report.time.WorkItemBasedGrouping"),
    JsonSubTypes.Type(value = FieldBasedGrouping::class, name = "jetbrains.charisma.smartui.report.time.FieldBasedGrouping")
)
interface Grouping : GroupByParameter