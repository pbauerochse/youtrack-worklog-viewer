package de.pbauerochse.worklogviewer.details

import de.pbauerochse.worklogviewer.datasource.DataSources
import de.pbauerochse.worklogviewer.tasks.Progress
import de.pbauerochse.worklogviewer.tasks.WorklogViewerTask
import de.pbauerochse.worklogviewer.timereport.Issue
import de.pbauerochse.worklogviewer.timereport.IssueWithWorkItems
import de.pbauerochse.worklogviewer.timereport.TimeRange
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import org.slf4j.LoggerFactory

/**
 * Fetches the [de.pbauerochse.worklogviewer.timereport.WorkItem]s for a single [Issue]
 */
class FetchWorkItemsForIssueTask(
    private val issue: Issue,
    private val timerange: TimeRange?
): WorklogViewerTask<IssueWithWorkItems>(getFormatted("issue.workitems.loading", issue.humanReadableId)) {

    constructor(issue: Issue): this(issue, null)

    override fun start(progress: Progress): IssueWithWorkItems {
        LOGGER.debug("Loading WorkItems for Issue $issue")
        val dataSource = DataSources.activeDataSource!!
        return dataSource.loadWorkItems(issue, timerange, progress)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(FetchWorkItemsForIssueTask::class.java)
    }
}