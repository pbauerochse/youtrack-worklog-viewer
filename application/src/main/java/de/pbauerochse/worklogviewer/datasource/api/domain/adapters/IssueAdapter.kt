package de.pbauerochse.worklogviewer.datasource.api.domain.adapters

import de.pbauerochse.worklogviewer.datasource.api.domain.PeriodIssueCustomField
import de.pbauerochse.worklogviewer.datasource.api.domain.ProjectTimeTrackingSettings
import de.pbauerochse.worklogviewer.datasource.api.domain.YouTrackIssue
import de.pbauerochse.worklogviewer.timereport.Field
import de.pbauerochse.worklogviewer.timereport.Issue
import de.pbauerochse.worklogviewer.timereport.Project
import de.pbauerochse.worklogviewer.timereport.Tag
import org.apache.commons.text.StringEscapeUtils
import java.net.URL
import java.time.ZonedDateTime

/**
 * Implementation of the [Issue] interface, that wraps around a [YouTrackIssue]
 */
class IssueAdapter(
    youtrackIssue: YouTrackIssue,
    projectTimeTrackingSettings: ProjectTimeTrackingSettings?,
    override val externalUrl: URL
) : Issue {

    override val id: String = youtrackIssue.id
    override val humanReadableId: String = youtrackIssue.idReadable
    override val issueNumber: Long = humanReadableId.substringAfterLast('-').toLong()
    override val title: String = youtrackIssue.summary ?: youtrackIssue.idReadable
    override val descriptionWithHtmlMarkup: String = youtrackIssue.description

    override val descriptionPlaintext: String
        get() = descriptionWithHtmlMarkup
            .let { StringEscapeUtils.unescapeHtml4(it) }
            .replace("\n", "")
            .replace("</li>", "</li>\n")
            .replace("<li>", "- ")
            .replace("</p>", "</p>\n\n")
            .replace("<br/>", "\n")
            .replace(Regex("<[^>]*>"), "")
            .replace(Regex("[\\t\\x0B\\f\\x20]{2,}"), " ")
            .trim()

    override val project: Project = youtrackIssue.project?.let { Project(it.id, it.name ?: it.id, it.shortName ?: it.id) } ?: UNKNOWN_PROJECT
    override val resolutionDate: ZonedDateTime? = youtrackIssue.resolveDate
    override val tags: List<Tag> = youtrackIssue.tags.map { Tag(label = it.name, backgroundColor = it.color.backgroundColor, foregroundColor = it.color.foregroundColor) }
    override val estimationInHours: Long? = findEstimation(youtrackIssue, projectTimeTrackingSettings)

    override val fields: List<Field> = youtrackIssue.customFields.map { youtrackField ->
        val fieldValues = youtrackField.valuesAsString.asSequence()
            .mapNotNull { value -> value.takeIf { it.isNotBlank() } }
            .toList()

        Field(youtrackField.localizedName ?: youtrackField.name!!, fieldValues)
    }

    private fun findEstimation(youtrackIssue: YouTrackIssue, projectTimeTrackingSettings: ProjectTimeTrackingSettings?): Long? {
        return projectTimeTrackingSettings
            ?.estimate
            ?.field
            ?.let { projectEstimateField -> youtrackIssue.customFields.find { issueField -> issueField.name == projectEstimateField.name } }
            ?.let { it as PeriodIssueCustomField }
            ?.value
            ?.minutes

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IssueAdapter) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Issue(externalUrl=$externalUrl, id='$id', humanReadableId='$humanReadableId', title='$title')"
    }

    companion object {
        private val UNKNOWN_PROJECT: Project = Project("---", "---", "---")
    }
}