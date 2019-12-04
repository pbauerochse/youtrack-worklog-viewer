package de.pbauerochse.worklogviewer.fx.plugins

import de.pbauerochse.worklogviewer.plugins.state.TabContext
import de.pbauerochse.worklogviewer.plugins.state.UIState
import java.time.LocalDate

class WorklogviewerUiState(
    override val currentlyVisibleTab: TabContext?,
    override val currentStartValue: LocalDate?,
    override val currentEndValue: LocalDate?
) : UIState