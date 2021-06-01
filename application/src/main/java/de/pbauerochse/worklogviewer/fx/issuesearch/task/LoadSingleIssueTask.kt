package de.pbauerochse.worklogviewer.fx.issuesearch.task

import de.pbauerochse.worklogviewer.datasource.TimeTrackingDataSource
import de.pbauerochse.worklogviewer.fx.tasks.WorklogViewerTask
import de.pbauerochse.worklogviewer.report.Issue
import de.pbauerochse.worklogviewer.tasks.Progress
import de.pbauerochse.worklogviewer.util.FormattingUtil
import org.slf4j.LoggerFactory

/**
 * Loads a single Issue from the [TimeTrackingDataSource]
 */
class LoadSingleIssueTask(
    private val issueId: String,
    private val connector: TimeTrackingDataSource
) : WorklogViewerTask<Issue?>(FormattingUtil.getFormatted("dialog.issuesearch.task.single.title", issueId)) {

    override fun start(progress: Progress): Issue? {
        LOGGER.info("Loading single Issue $issueId")
        return connector.loadIssue(issueId, progress)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(LoadSingleIssueTask::class.java)
    }
}
