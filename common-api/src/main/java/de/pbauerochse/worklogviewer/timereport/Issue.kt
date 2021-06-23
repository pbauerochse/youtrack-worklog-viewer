package de.pbauerochse.worklogviewer.timereport

import java.net.URL
import java.time.ZonedDateTime

interface Issue: Comparable<Issue> {

    /**
     * Unique ID to distinguish this [Issue]
     * from others. This field contains the
     * technical id (e.g. Database Id from the source system)
     */
    val id: String

    /**
     * A number mainly used for comparison / ordering
     * purposes. Should also unique per [Issue]
     */
    val issueNumber: Long

    /**
     * A short label which will be used in the frontend
     * of the application. Should be short enough to not
     * break the UI but still allow the user to quickly
     * identify it at first glance.
     */
    val humanReadableId: String

    /**
     * The location where the details of this [Issue] can
     * be viewed in the browser
     */
    val externalUrl: URL

    /**
     * The title / summary of this [Issue]
     */
    val title: String

    /**
     * The description of the [Issue] which may contain
     * HTML Markup
     */
    val descriptionWithHtmlMarkup: String

    /**
     * The description of the [Issue] with all
     * HTML Tags stripped.
     */
    val descriptionPlaintext: String
        get() = descriptionWithHtmlMarkup
            .replace("\n", "")
            .replace("</li>", "\n")
            .replace("<li>", "- ")
            .replace("</p>", "\n\n")
            .replace("<br/>", "\n")
            .replace(Regex("<[^>]*>"), "")

    /**
     * The [Project] this [Issue] belongs to
     */
    val project: Project

    /**
     * if this [Issue] has been marked as resolved,
     * this field contains the timestamp when this
     * has been done, else null
     */
    val resolutionDate: ZonedDateTime?

    /**
     * additional properties as Key-Value Pairs
     */
    val fields: List<Field>

    /**
     * Optional tags for this issue
     */
    val tags: List<Tag>

    /**
     * The `humanReadableId` and the `title` combined in one field
     */
    val fullTitle: String
        get() = "$humanReadableId - $title"

    override fun compareTo(other: Issue): Int {
        return when (val byProject = project.shortName.compareTo(other.project.shortName)) {
            0 -> issueNumber.compareTo(other.issueNumber)
            else -> byProject
        }
    }
}