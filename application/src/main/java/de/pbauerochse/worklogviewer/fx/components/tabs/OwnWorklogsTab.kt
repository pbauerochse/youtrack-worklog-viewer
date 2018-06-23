package de.pbauerochse.worklogviewer.fx.components.tabs

import de.pbauerochse.worklogviewer.fx.components.statistics.data.TaskCountByProjectAndUserStatisticData
import de.pbauerochse.worklogviewer.fx.components.statistics.data.TaskCountByUserAndProjectStatisticData
import de.pbauerochse.worklogviewer.fx.components.statistics.panels.TaskCountByUserAndProjectStatistics
import de.pbauerochse.worklogviewer.fx.components.statistics.panels.TimePerProjectAndUserGraphStatistics
import de.pbauerochse.worklogviewer.fx.components.statistics.panels.TimePerUserAndProjectGraphStatistics
import de.pbauerochse.worklogviewer.report.Issue
import de.pbauerochse.worklogviewer.report.TimeReport
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import javafx.scene.Node
import org.slf4j.LoggerFactory

/**
 * Tab, that only shows the own worklogs,
 * so only the times the user tracked
 */
internal class OwnWorklogsTab : WorklogsTab(LABEL) {

    fun update(report: TimeReport) {
        LOGGER.debug("Showing own worklogs")
        update(LABEL, report.parameters, extractOwnWorklogs(report))
    }

    override fun getStatistics(issues: List<Issue>): List<Node> {
        val dataByUser = TaskCountByUserAndProjectStatisticData(issues)
        val dataByProject = TaskCountByProjectAndUserStatisticData(issues)
        return arrayListOf(
            TaskCountByUserAndProjectStatistics(dataByUser),
            TimePerProjectAndUserGraphStatistics(dataByUser),
            TimePerUserAndProjectGraphStatistics(dataByProject)
        )
    }

    private fun extractOwnWorklogs(report: TimeReport): List<Issue> {
        return report.data.projects
            .filter { it.hasTicketsWithOwnWorklogs() }
            .flatMap { getEntriesRelatedToUser(it) }
            .sorted()
    }

    private fun getEntriesRelatedToUser(it: Project): List<Issue> {
        return it.issues
            .filter { it.hasOwnWorklogs() }
            .map {
                Issue(it, it.worklogItems.filter { it.isOwn() })
            }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(OwnWorklogsTab::class.java)
        private val LABEL = getFormatted("view.main.tabs.own")
    }
}
