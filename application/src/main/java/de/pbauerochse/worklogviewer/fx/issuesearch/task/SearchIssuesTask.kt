package de.pbauerochse.worklogviewer.fx.issuesearch.task

import de.pbauerochse.worklogviewer.connector.YouTrackConnector
import de.pbauerochse.worklogviewer.fx.tasks.WorklogViewerTask
import de.pbauerochse.worklogviewer.report.Issue
import de.pbauerochse.worklogviewer.tasks.Progress
import org.slf4j.LoggerFactory

class SearchIssuesTask(
        private val query: String,
        private val offset : Int,
        private val connector : YouTrackConnector
) : WorklogViewerTask<List<Issue>>("TODO Issue Search") {

    val isNewSearch = offset == 0

    override fun start(progress: Progress): List<Issue> {
        LOGGER.info("Searching for Issues with query='$query' and offset=$offset")
        return connector.searchIssues(query, offset, progress)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(SearchIssuesTask::class.java)
    }
}
