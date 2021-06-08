package de.pbauerochse.worklogviewer.datasource.api.domain.adapters

import de.pbauerochse.worklogviewer.datasource.api.domain.YouTrackIssue
import de.pbauerochse.worklogviewer.timereport.Field
import de.pbauerochse.worklogviewer.timereport.Issue
import de.pbauerochse.worklogviewer.timereport.Project
import java.net.URL
import java.time.ZonedDateTime

/**
 * Implementation of the [Issue] interface, that wraps around a [YouTrackIssue]
 */
class IssueAdapter(
    youtrackIssue: YouTrackIssue,
    override val externalUrl: URL
): Issue {

    override val id: String = youtrackIssue.id
    override val humanReadableId: String = youtrackIssue.idReadable
    override val issueNumber: Long = humanReadableId.substringAfterLast('-').toLong()
    override val title: String = youtrackIssue.summary ?: youtrackIssue.idReadable
    override val description: String = youtrackIssue.description
    override val project: Project = youtrackIssue.project?.let { Project(it.id, it.name ?: it.id, it.shortName ?: it.id) } ?: UNKNOWN_PROJECT
    override val resolutionDate: ZonedDateTime? = youtrackIssue.resolveDate
    override val fields: List<Field> = youtrackIssue.customFields.map { youtrackField ->
        val fieldValues = youtrackField.values.asSequence()
            .mapNotNull { value -> value.value.takeIf { !it.isNullOrBlank() } }
            .toList()

        Field(youtrackField.localizedName ?: youtrackField.name!!, fieldValues)
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