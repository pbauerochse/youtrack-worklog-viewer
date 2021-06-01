package de.pbauerochse.worklogviewer.fx.workitem.add

import de.pbauerochse.worklogviewer.connector.YouTrackConnectorLocator
import de.pbauerochse.worklogviewer.connector.workitem.AddWorkItemRequest
import de.pbauerochse.worklogviewer.connector.workitem.AddWorkItemResult
import de.pbauerochse.worklogviewer.fx.tasks.WorklogViewerTask
import de.pbauerochse.worklogviewer.tasks.Progress
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted

class AddWorkItemTask(private val request: AddWorkItemRequest) : WorklogViewerTask<AddWorkItemResult>(getFormatted("task.addworkitem.title")) {

    override fun start(progress: Progress): AddWorkItemResult {
        progress.setProgress(getFormatted("task.addworkitem.creating"), 0.1)
        val service = YouTrackConnectorLocator.getActiveConnector()!!
        val response = service.addWorkItem(request, progress)
        progress.setProgress(getFormatted("task.addworkitem.done"), 100)
        return response
    }
}