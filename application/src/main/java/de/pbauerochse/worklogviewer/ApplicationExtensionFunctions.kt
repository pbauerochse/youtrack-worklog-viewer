package de.pbauerochse.worklogviewer

import de.pbauerochse.worklogviewer.timereport.Issue
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