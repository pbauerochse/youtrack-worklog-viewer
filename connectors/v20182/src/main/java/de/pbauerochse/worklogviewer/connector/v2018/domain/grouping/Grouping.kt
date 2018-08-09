package de.pbauerochse.worklogviewer.connector.v2018.domain.grouping

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import de.pbauerochse.worklogviewer.connector.GroupByParameter
import de.pbauerochse.worklogviewer.connector.v2018.domain.issue.YouTrackIssue
import de.pbauerochse.worklogviewer.connector.v2018.domain.issue.YouTrackWorklogItem
import de.pbauerochse.worklogviewer.report.Issue

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "\$type")
@JsonSubTypes(
    JsonSubTypes.Type(value = FieldBasedGrouping::class, name = "jetbrains.youtrack.reports.impl.time.gap.FieldBasedGrouping")
)
interface Grouping : GroupByParameter {

    /**
     * Determines the grouping key for the given YouTrackWorklogItem
     * as of version 2018.2 YouTrack will not return the
     * grouping criteria. It has to be implemented in this application
     */
    fun getGroupingKey(
        worklogItem: YouTrackWorklogItem,
        issue: Issue,
        youtrackIssue: YouTrackIssue
    ): String?

}