package de.pbauerochse.worklogviewer.plugins.state

import de.pbauerochse.worklogviewer.timereport.TimeReport

interface WorklogViewerState {

    /**
     * the raw TimeReport as it was provided by the
     * [de.pbauerochse.worklogviewer.datasource.TimeTrackingDataSource]
     */
    val currentTimeReport: TimeReport?

    /**
     * Provides access the state the worklog
     * viewer user interface is currently in
     */
    val ui: UIState

}