package de.pbauerochse.worklogviewer

import de.pbauerochse.worklogviewer.connector.workitem.MinimalWorklogItem
import de.pbauerochse.worklogviewer.report.Issue
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
    onAction = EventHandler { Platform.runLater { WorklogViewer.getInstance().hostServices.showDocument(url) } }
}

/**
 * Opens the issue in the browser
 */
fun Issue.openInBrowser() {
    Platform.runLater { WorklogViewer.getInstance().hostServices.showDocument(this.getYouTrackLink().toExternalForm()) }
}

/**
 * Constructs a link to the YouTrack Issue
 * by taking the base URL from the Settings
 * and adding the issue path
 */
fun Issue.getYouTrackLink(): URL {
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
 * adds the given [MinimalWorklogItem] into this [TimeReport]
 * by creating a copy with the [MinimalWorklogItem] applied
 * to the clone
 */
fun TimeReport.withAddedWorkItem(newWorkitem: MinimalWorklogItem) : TimeReport {
    TODO("Not implemented yet")
}
