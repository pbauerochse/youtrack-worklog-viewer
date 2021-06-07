package de.pbauerochse.worklogviewer.issue.details

import de.pbauerochse.worklogviewer.datasource.DataSources
import de.pbauerochse.worklogviewer.fx.tasks.WorklogViewerTask
import de.pbauerochse.worklogviewer.tasks.Progress
import de.pbauerochse.worklogviewer.timereport.Issue
import de.pbauerochse.worklogviewer.timereport.IssueWithWorkItems
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import org.slf4j.LoggerFactory

/**
 * Fetches the [de.pbauerochse.worklogviewer.timereport.WorkItem]s for a single [Issue]
 */
class FetchWorkItemsForIssueTask(private val issue: Issue): WorklogViewerTask<IssueWithWorkItems>(getFormatted("issue.workitems.loading", issue.humanReadableId)) {

    override fun start(progress: Progress): IssueWithWorkItems {
        LOGGER.debug("Loading WorkItems for Issue $issue")
        val dataSource = DataSources.activeDataSource!!
        return dataSource.loadWorkItems(issue, progress)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(FetchWorkItemsForIssueTask::class.java)
    }
}