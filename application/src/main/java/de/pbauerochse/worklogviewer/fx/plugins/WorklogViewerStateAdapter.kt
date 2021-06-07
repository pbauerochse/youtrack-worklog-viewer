package de.pbauerochse.worklogviewer.fx.plugins

import de.pbauerochse.worklogviewer.plugins.state.UIState
import de.pbauerochse.worklogviewer.plugins.state.WorklogViewerState
import de.pbauerochse.worklogviewer.timereport.TimeReport

class WorklogViewerStateAdapter(
    override val currentTimeReport: TimeReport?,
    override val ui: UIState
) : WorklogViewerState