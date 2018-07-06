package de.pbauerochse.worklogviewer.connector.v2018.domain.report

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import de.pbauerochse.worklogviewer.connector.GroupByParameter

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "\$type")
@JsonSubTypes(
    JsonSubTypes.Type(value = WorkItemBasedGrouping::class, name = "jetbrains.youtrack.reports.impl.time.gap.WorkItemBasedGrouping"),
    JsonSubTypes.Type(value = FieldBasedGrouping::class, name = "jetbrains.youtrack.reports.impl.time.gap.FieldBasedGrouping")
)
interface Grouping : GroupByParameter