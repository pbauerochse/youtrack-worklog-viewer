package de.pbauerochse.worklogviewer.fx.plugins

import de.pbauerochse.worklogviewer.http.Http
import de.pbauerochse.worklogviewer.plugins.dialog.WorklogViewerDialog
import de.pbauerochse.worklogviewer.plugins.formatter.YouTrackWorktimeFormatter
import de.pbauerochse.worklogviewer.plugins.tasks.TaskRunner
import de.pbauerochse.worklogviewer.plugins.tools.WorklogViewerTools

class WorklogViewerToolsAdapter : WorklogViewerTools {
    override val http: Http
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val timesFormatter: YouTrackWorktimeFormatter
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val taskRunner: TaskRunner
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val dialog: WorklogViewerDialog
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
}