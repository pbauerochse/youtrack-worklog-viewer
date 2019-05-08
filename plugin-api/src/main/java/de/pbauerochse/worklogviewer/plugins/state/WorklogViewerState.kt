package de.pbauerochse.worklogviewer.plugins.state

import de.pbauerochse.worklogviewer.report.TimeReport

interface WorklogViewerState {
    val currentTimeReport : TimeReport?
    val currentlyVisibleIssues : TabContext?
}