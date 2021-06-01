package de.pbauerochse.worklogviewer.plugins.actions

import de.pbauerochse.worklogviewer.datasource.TimeTrackingDataSource
import de.pbauerochse.worklogviewer.plugins.dialog.WorklogViewerDialog
import de.pbauerochse.worklogviewer.plugins.formatter.YouTrackWorktimeFormatter
import de.pbauerochse.worklogviewer.plugins.state.WorklogViewerState
import de.pbauerochse.worklogviewer.plugins.tasks.TaskRunner

interface PluginActionContext {

    /**
     * The current application state
     */
    val state: WorklogViewerState

    /**
     * a http connector, to execute http calls to the
     * configured YouTrack API. The http connector already
     * contains the login data of the user. Use responsibly!
     */
    val connector: TimeTrackingDataSource

    /**
     * a time formatter to convert durations in minutes to the default
     * YouTrack format (e.g. "1d 5h 15m")
     */
    val timesFormatter: YouTrackWorktimeFormatter

    /**
     * allows executing async tasks
     */
    val taskRunner: TaskRunner

    /**
     * allows opening a popup and / or a file chooser dialog
     */
    val dialog: WorklogViewerDialog
}