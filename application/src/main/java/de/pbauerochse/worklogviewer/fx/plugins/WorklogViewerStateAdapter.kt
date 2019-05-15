package de.pbauerochse.worklogviewer.fx.plugins

import de.pbauerochse.worklogviewer.plugins.state.TabContext
import de.pbauerochse.worklogviewer.plugins.state.WorklogViewerState
import de.pbauerochse.worklogviewer.report.TimeReport

class WorklogViewerStateAdapter(
    override val currentTimeReport: TimeReport?,
    override val currentlyVisibleTab: TabContext?
) : WorklogViewerState