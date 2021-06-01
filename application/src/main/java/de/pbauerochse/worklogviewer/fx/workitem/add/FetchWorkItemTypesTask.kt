package de.pbauerochse.worklogviewer.fx.workitem.add

import de.pbauerochse.worklogviewer.connector.YouTrackConnectorLocator
import de.pbauerochse.worklogviewer.fx.tasks.WorklogViewerTask
import de.pbauerochse.worklogviewer.report.WorkItemType
import de.pbauerochse.worklogviewer.tasks.Progress
import de.pbauerochse.worklogviewer.util.FormattingUtil

/**
 * Task, that loads the valid [de.pbauerochse.worklogviewer.report.WorklogItem] Types
 */
class FetchWorkItemTypesTask(private val projectId: String) : WorklogViewerTask<List<WorkItemType>>(FormattingUtil.getFormatted("task.addworkitem.task.workitems.title")) {

    override fun start(progress: Progress): List<WorkItemType> {
        progress.setProgress(FormattingUtil.getFormatted("task.addworkitem.task.workitems.loading"), 0.1)
        val service = YouTrackConnectorLocator.getActiveConnector()!!
        val response = service.getWorkItemTypes(projectId, progress)
        progress.setProgress(FormattingUtil.getFormatted("task.addworkitem.task.workitems.done"), 100)
        return response
    }
}