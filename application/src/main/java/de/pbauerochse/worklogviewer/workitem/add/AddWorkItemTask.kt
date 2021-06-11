package de.pbauerochse.worklogviewer.workitem.add

import de.pbauerochse.worklogviewer.datasource.AddWorkItemRequest
import de.pbauerochse.worklogviewer.datasource.AddWorkItemResult
import de.pbauerochse.worklogviewer.datasource.DataSources
import de.pbauerochse.worklogviewer.tasks.Progress
import de.pbauerochse.worklogviewer.tasks.WorklogViewerTask
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted

class AddWorkItemTask(private val request: AddWorkItemRequest) : WorklogViewerTask<AddWorkItemResult>(getFormatted("task.addworkitem.title")) {

    override fun start(progress: Progress): AddWorkItemResult {
        progress.setProgress(getFormatted("task.addworkitem.creating"), 0.1)
        val service = DataSources.activeDataSource!!
        val response = service.addWorkItem(request, progress)
        progress.setProgress(getFormatted("task.addworkitem.done"), 100)
        return response
    }
}