package de.pbauerochse.worklogviewer.plugins.state

import de.pbauerochse.worklogviewer.report.TimeReport

interface WorklogViewerState {

    /**
     * the raw TimeReport as it was provided by the
     * [de.pbauerochse.worklogviewer.connector.YouTrackConnector]
     */
    val currentTimeReport: TimeReport?

    /**
     * The data context of the currently visible tab / project
     * in the main application window
     */
    val currentlyVisibleTab: TabContext?

}