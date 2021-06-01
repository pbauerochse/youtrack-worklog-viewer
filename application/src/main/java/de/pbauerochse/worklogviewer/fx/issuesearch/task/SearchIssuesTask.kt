package de.pbauerochse.worklogviewer.fx.issuesearch.task

import de.pbauerochse.worklogviewer.datasource.TimeTrackingDataSource
import de.pbauerochse.worklogviewer.fx.tasks.WorklogViewerTask
import de.pbauerochse.worklogviewer.report.Issue
import de.pbauerochse.worklogviewer.tasks.Progress
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import org.slf4j.LoggerFactory

/**
 * Task that searches a List of Issues by a given query
 */
class SearchIssuesTask(
        private val query: String,
        private val offset : Int,
        private val connector : TimeTrackingDataSource
) : WorklogViewerTask<List<Issue>>(getFormatted("dialog.issuesearch.task.title")) {

    val isNewSearch = offset == 0

    override fun start(progress: Progress): List<Issue> {
        LOGGER.info("Searching for Issues with query='$query' and offset=$offset")
        return connector.searchIssues(query, offset, progress)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SearchIssuesTask::class.java)
    }
}
