package de.pbauerochse.worklogviewer.plugins.state

import de.pbauerochse.worklogviewer.timereport.view.ReportView

interface TabContext {

    /**
     * A flattened view of the `currentTimeReport` where
     * any grouping has already been applied. This view
     * represents the table layout currently shown in the
     * main application
     */
    val view: ReportView
}