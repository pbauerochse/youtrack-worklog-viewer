package de.pbauerochse.worklogviewer.connector.v2018.domain.grouping

import com.fasterxml.jackson.annotation.*
import de.pbauerochse.worklogviewer.connector.v2018.domain.groupby.GroupingField
import de.pbauerochse.worklogviewer.connector.v2018.domain.issue.YouTrackIssue
import de.pbauerochse.worklogviewer.connector.v2018.domain.issue.YouTrackWorklogItem
import de.pbauerochse.worklogviewer.report.Issue
import org.slf4j.LoggerFactory

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class WorkItemBasedGrouping @JsonCreator constructor(
    @JsonProperty("field") val field: GroupingField
) : Grouping {

    @JsonIgnore
    override val id: String = field.id

    @JsonIgnore
    override fun getLabel(): String = field.presentation

    override fun getGroupingKey(
        worklogItem: YouTrackWorklogItem,
        issue: Issue,
        youtrackIssue: YouTrackIssue
    ): String? {
        LOGGER.debug("Determining groupingKey")
        return null
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(WorkItemBasedGrouping::class.java)
    }
}