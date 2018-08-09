package de.pbauerochse.worklogviewer.connector.v2018.domain.grouping

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import de.pbauerochse.worklogviewer.connector.v2018.domain.groupby.GroupingField
import de.pbauerochse.worklogviewer.connector.v2018.domain.issue.YouTrackIssue
import de.pbauerochse.worklogviewer.connector.v2018.domain.issue.YouTrackWorklogItem
import de.pbauerochse.worklogviewer.report.Issue
import org.slf4j.LoggerFactory

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
class FieldBasedGrouping @JsonCreator constructor(
    @JsonProperty("field") private val field: GroupingField
) : Grouping {

    @JsonProperty("id")
    override val id: String = field.id

    @JsonProperty("presentation")
    override fun getLabel(): String = field.presentation

    override fun getGroupingKey(worklogItem: YouTrackWorklogItem, issue: Issue, youtrackIssue: YouTrackIssue): String? {
        LOGGER.debug("Determining groupingKey for field $field")
        val possibleFieldNames = field.getPossibleNames()
        val field = youtrackIssue.fields.find { possibleFieldNames.contains(it.name) }
        return field?.toGroupByKey()
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(FieldBasedGrouping::class.java)
    }
}