package de.pbauerochse.worklogviewer.fx.components.tabs

import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import de.pbauerochse.worklogviewer.youtrack.TimeReport
import org.slf4j.LoggerFactory

/**
 * Tab, that shows all worklogs contained in the
 * [TimeReport], no matter what author or project
 */
internal class AllWorklogsTab : WorklogsTab(getFormatted("view.main.tabs.all")) {

    fun update(report: TimeReport) {
        LOGGER.debug("Showing all worklogs")
        val allIssues = report.data.projects
            .filter { it.hasTicketsWithOwnWorklogs() }
            .flatMap { it.issues }
            .sorted()

        update(text, report.parameters, allIssues)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(AllWorklogsTab::class.java)
    }
}
