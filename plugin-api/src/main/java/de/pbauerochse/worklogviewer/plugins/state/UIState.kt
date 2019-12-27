package de.pbauerochse.worklogviewer.plugins.state

import de.pbauerochse.worklogviewer.report.TimeRange

/**
 * Contains state of the youtrack worklog viewer ui
 */
interface UIState {

    /**
     * The data context of the currently visible tab / project
     * in the main application window
     */
    val currentlyVisibleTab: TabContext?

    /**
     * The currently selected TimeRange in the UI date pickers.
     */
    val timeRange: TimeRange

}