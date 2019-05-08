package de.pbauerochse.worklogviewer.plugins.tools

import de.pbauerochse.worklogviewer.http.Http
import de.pbauerochse.worklogviewer.plugins.dialog.WorklogViewerDialog
import de.pbauerochse.worklogviewer.plugins.formatter.YouTrackWorktimeFormatter
import de.pbauerochse.worklogviewer.plugins.tasks.TaskRunner

/**
 * Plugin tools to interact with the main application
 */
interface WorklogViewerTools {

    /**
     * a http connector, to execute http calls to the
     * configured YouTrack API. The http connector already
     * contains the login data of the user. Use responsibly!
     */
    val http : Http

    /**
     * a time formatter to convert durations in minutes to the default
     * YouTrack format (e.g. "1d 5h 15m")
     */
    val timesFormatter : YouTrackWorktimeFormatter

    /**
     * allows executing async tasks
     */
    val taskRunner : TaskRunner

    /**
     * allows opening a popup and / or a file chooser dialog
     */
    val dialog : WorklogViewerDialog
}