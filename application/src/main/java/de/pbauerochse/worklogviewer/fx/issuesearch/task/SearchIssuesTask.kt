package de.pbauerochse.worklogviewer.fx.issuesearch.task

import de.pbauerochse.worklogviewer.datasource.TimeTrackingDataSource
import de.pbauerochse.worklogviewer.fx.tasks.WorklogViewerTask
import de.pbauerochse.worklogviewer.tasks.Progress
import de.pbauerochse.worklogviewer.timereport.Issue
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import org.slf4j.LoggerFactory

/**
 * Task that searches a List of Issues by a given query
 */
class SearchIssuesTask(
    private val query: String,
    private val offset: Int,
    private val maxResults: Int,
    private val dataSource: TimeTrackingDataSource
) : WorklogViewerTask<List<Issue>>(getFormatted("dialog.issuesearch.task.title")) {

    val isNewSearch = offset == 0

    override fun start(progress: Progress): List<Issue> {
        LOGGER.info("Searching for Issues with query='$query' and offset=$offset and maxResults=$maxResults")
        return dataSource.searchIssues(query, offset, maxResults, progress)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SearchIssuesTask::class.java)
    }
}
