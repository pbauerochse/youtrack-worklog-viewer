package de.pbauerochse.worklogviewer

import de.pbauerochse.worklogviewer.datasource.AddWorkItemResult
import de.pbauerochse.worklogviewer.timereport.Issue
import de.pbauerochse.worklogviewer.timereport.IssueWithWorkItems
import de.pbauerochse.worklogviewer.timereport.TimeReport
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
 * Opens the [Issue] in the browser
 */
fun Issue.openInBrowser() {
    externalUrl.openInBrowser()
}

fun URL.openInBrowser() {
    Platform.runLater { WorklogViewer.instance.hostServices.showDocument(toExternalForm()) }
}

fun String.toURL(): URL = URL(this)

/**
 * adds the given [AddWorkItemResult] into this [TimeReport]
 * by creating a copy with the [AddWorkItemResult] applied
 * to the clone
 */
fun TimeReport.addWorkItem(newWorkitem: AddWorkItemResult): TimeReport {
    TODO("REPLACE WITH FRESHLY LOADED ISSUE FROM RESULT")
//    return if (reportParameters.timerange.includes(newWorkitem.date)) {
//        // only update if the newly created item is for the current timerange
//        val issueList = issues.toMutableList()
//        val issue = issues.find { it.issue.id == newWorkitem.issue.id } ?: createDetachedIssue(newWorkitem).apply {
//            // issue to added work item was not
//            // present when initial report was fetched
//            // add "mock" item
//            issueList.add(this)
//        }
//
//        val newWorkItem = WorkItem(
//            issue = issue,
//            date = newWorkitem.date,
//            description = newWorkitem.text,
//            durationInMinutes = newWorkitem.durationInMinutes,
//            user = newWorkitem.user,
//            workType = newWorkitem.workType
//        )
//        issue.worklogItems.add(newWorkItem)
//
//        return TimeReport(reportParameters, issueList)
//    } else this
}

private fun createDetachedIssue(newWorkitem: AddWorkItemResult): IssueWithWorkItems {
    TODO("NOP")
//    return Issue(
//        newWorkitem.issue.id,
//        newWorkitem.issue.summary ?: "",
//        newWorkitem.issue.project!!,
//        newWorkitem.issue.description ?: "",
//        emptyList()
//    )
}

fun <T> MutableCollection<T>.addIfMissing(item : T) {
    if (!contains(item)) {
        this.add(item)
    }
}
