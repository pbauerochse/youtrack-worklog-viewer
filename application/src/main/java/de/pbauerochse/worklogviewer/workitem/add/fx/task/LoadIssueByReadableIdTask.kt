package de.pbauerochse.worklogviewer.workitem.add.fx.task

import de.pbauerochse.worklogviewer.datasource.DataSources
import de.pbauerochse.worklogviewer.tasks.Progress
import de.pbauerochse.worklogviewer.tasks.WorklogViewerTask
import de.pbauerochse.worklogviewer.timereport.Issue
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import org.slf4j.LoggerFactory

/**
 * Task that loads a single Issue by its humanReadableId
 */
class LoadIssueByReadableIdTask(private val readableId: String) : WorklogViewerTask<Issue>(getFormatted("dialog.issue.byid.task.title", readableId)) {

    override fun start(progress: Progress): Issue {
        LOGGER.info("Loading Issue $readableId")
        val dataSource = DataSources.activeDataSource!!
        return dataSource.loadIssue(readableId, progress)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(LoadIssueByReadableIdTask::class.java)
    }
}
