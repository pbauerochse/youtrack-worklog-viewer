package de.pbauerochse.worklogviewer.fx.plugins

import de.pbauerochse.worklogviewer.plugins.state.TabContext
import de.pbauerochse.worklogviewer.plugins.state.WorklogViewerState
import de.pbauerochse.worklogviewer.report.TimeReport

class WorklogViewerStateAdapter : WorklogViewerState {

    override val currentTimeReport: TimeReport?
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val currentlyVisibleIssues: TabContext?
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
}