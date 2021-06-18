package de.pbauerochse.worklogviewer.fx.components.tabs

import de.pbauerochse.worklogviewer.fx.components.statistics.data.TaskCountByProjectAndUserStatisticData
import de.pbauerochse.worklogviewer.fx.components.statistics.data.TaskCountByUserAndProjectStatisticData
import de.pbauerochse.worklogviewer.fx.components.statistics.panels.OvertimeStatisticsPane
import de.pbauerochse.worklogviewer.fx.components.statistics.panels.TaskCountByUserAndProjectStatistics
import de.pbauerochse.worklogviewer.fx.components.statistics.panels.TimePerProjectAndUserGraphStatistics
import de.pbauerochse.worklogviewer.fx.components.statistics.panels.TimePerUserAndProjectGraphStatistics
import de.pbauerochse.worklogviewer.timereport.IssueWithWorkItems
import de.pbauerochse.worklogviewer.timereport.TimeReport
import de.pbauerochse.worklogviewer.timereport.view.ReportView
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import de.pbauerochse.worklogviewer.view.grouping.Grouping
import javafx.scene.Node
import org.slf4j.LoggerFactory

/**
 * Tab, that only shows the own worklogs,
 * so only the times the user tracked
 */
internal class OwnWorklogsTab : WorklogsTab(LABEL) {

    fun update(report: TimeReport, grouping: Grouping) {
        LOGGER.debug("Showing own worklogs")
        update(LABEL, extractOwnWorklogs(report), report.reportParameters, grouping)
    }

    override fun getStatistics(reportView: ReportView): List<Node> {
        val dataByUser = TaskCountByUserAndProjectStatisticData(reportView.issues)
        val dataByProject = TaskCountByProjectAndUserStatisticData(reportView.issues)

        return listOf(
            TaskCountByUserAndProjectStatistics(dataByUser),
            OvertimeStatisticsPane(reportView),
            TimePerProjectAndUserGraphStatistics(dataByUser),
            TimePerUserAndProjectGraphStatistics(dataByProject)
        )
    }

    private fun extractOwnWorklogs(report: TimeReport): List<IssueWithWorkItems> {
        return report.issues
            .filter { it.hasWorkItemsBelongingToCurrentUser }
            .map {
                val workItemsForUserOnly = it.workItems.filter { workItem -> workItem.belongsToCurrentUser }
                IssueWithWorkItems(it.issue, workItemsForUserOnly)
            }
            .sortedBy { it.issue }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(OwnWorklogsTab::class.java)
        private val LABEL = getFormatted("view.main.tabs.own")
    }
}
