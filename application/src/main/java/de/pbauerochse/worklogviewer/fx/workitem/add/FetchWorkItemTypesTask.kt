package de.pbauerochse.worklogviewer.fx.workitem.add

import de.pbauerochse.worklogviewer.datasource.DataSources
import de.pbauerochse.worklogviewer.tasks.Progress
import de.pbauerochse.worklogviewer.tasks.WorklogViewerTask
import de.pbauerochse.worklogviewer.timereport.WorkItemType
import de.pbauerochse.worklogviewer.util.FormattingUtil

/**
 * Task, that loads the valid [de.pbauerochse.worklogviewer.timereport.WorkItem] Types
 */
class FetchWorkItemTypesTask(private val projectId: String) : WorklogViewerTask<List<WorkItemType>>(FormattingUtil.getFormatted("task.addworkitem.task.workitems.title")) {

    override fun start(progress: Progress): List<WorkItemType> {
        progress.setProgress(FormattingUtil.getFormatted("task.addworkitem.task.workitems.loading"), 0.1)
        val service = DataSources.activeDataSource!!
        val response = service.getWorkItemTypes(projectId, progress)
        progress.setProgress(FormattingUtil.getFormatted("task.addworkitem.task.workitems.done"), 100)
        return response
    }
}