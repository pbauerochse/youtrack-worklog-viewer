package de.pbauerochse.worklogviewer

import de.pbauerochse.worklogviewer.connector.workitem.AddWorkItemResult
import de.pbauerochse.worklogviewer.report.Issue
import de.pbauerochse.worklogviewer.report.MinimalIssue
import de.pbauerochse.worklogviewer.report.TimeReport
import de.pbauerochse.worklogviewer.report.WorklogItem
import de.pbauerochse.worklogviewer.settings.SettingsUtil
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.scene.control.Hyperlink
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Kotlin convenience extension functions
 */

fun String.toLocalDate(): LocalDate? = LocalDate.parse(this, DateTimeFormatter.ISO_DATE)

fun LocalDate.toFormattedString(): String = format(DateTimeFormatter.ISO_DATE)

fun Hyperlink.setHref(url: String) {
    onAction = EventHandler { Platform.runLater { WorklogViewer.instance.hostServices.showDocument(url) } }
}

/**
 * Opens the issue in the browser
 */
fun MinimalIssue.openInBrowser() {
    Platform.runLater { WorklogViewer.instance.hostServices.showDocument(this.getYouTrackLink().toExternalForm()) }
}

/**
 * Constructs a link to the YouTrack Issue
 * by taking the base URL from the Settings
 * and adding the issue path
 */
fun MinimalIssue.getYouTrackLink(): URL {
    return URL(SettingsUtil.settings.youTrackConnectionSettings.baseUrl, "/issue/$id#tab=Time%20Tracking")
}

/**
 * Returns true if the username of any of the
 * WorklogItems in this Issue matches the
 * username defined in the Settings
 */
fun Issue.hasOwnWorklogs(): Boolean = worklogItems.any { it.isOwn() }

/**
 * Returns true when the username matches the username
 * defined in the Settings dialog
 */
fun WorklogItem.isOwn(): Boolean {
    val ownUsername = SettingsUtil.settings.youTrackConnectionSettings.username
    return user.username == ownUsername
}

fun String.toURL(): URL = URL(this)

/**
 * adds the given [AddWorkItemResult] into this [TimeReport]
 * by creating a copy with the [AddWorkItemResult] applied
 * to the clone
 */
fun TimeReport.addWorkItem(newWorkitem: AddWorkItemResult): TimeReport {
    return if (reportParameters.timerange.includes(newWorkitem.date)) {
        // only update if the newly created item is for the current timerange
        val issueList = issues.toMutableList()
        val issue = issues.find { it.id == newWorkitem.issue.id } ?: createDetachedIssue(newWorkitem).apply {
            // issue to added work item was not
            // present when initial report was fetched
            // add "mock" item
            issueList.add(this)
        }

        val newWorkItem = WorklogItem(
            issue = issue,
            date = newWorkitem.date,
            description = newWorkitem.text,
            durationInMinutes = newWorkitem.durationInMinutes,
            user = newWorkitem.user,
            workType = newWorkitem.workType
        )
        issue.worklogItems.add(newWorkItem)

        return TimeReport(reportParameters, issueList)
    } else this
}

private fun createDetachedIssue(newWorkitem: AddWorkItemResult): Issue {
    return Issue(
        newWorkitem.issue.id,
        newWorkitem.issue.summary ?: "",
        newWorkitem.issue.project!!,
        newWorkitem.issue.description ?: "",
        emptyList()
    )
}

fun <T> MutableCollection<T>.addIfMissing(item : T) {
    if (!contains(item)) {
        this.add(item)
    }
}
