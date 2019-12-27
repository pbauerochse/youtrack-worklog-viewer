package de.pbauerochse.worklogviewer.fx.plugins

import de.pbauerochse.worklogviewer.plugins.state.TabContext
import de.pbauerochse.worklogviewer.plugins.state.UIState
import de.pbauerochse.worklogviewer.report.TimeRange

class WorklogviewerUiState(
    override val currentlyVisibleTab: TabContext?,
    override val timeRange: TimeRange
) : UIState