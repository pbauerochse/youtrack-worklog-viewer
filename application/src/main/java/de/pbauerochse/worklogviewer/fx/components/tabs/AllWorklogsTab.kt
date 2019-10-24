package de.pbauerochse.worklogviewer.fx.components.tabs

import de.pbauerochse.worklogviewer.fx.components.statistics.data.TaskCountByProjectAndUserStatisticData
import de.pbauerochse.worklogviewer.fx.components.statistics.data.TaskCountByUserAndProjectStatisticData
import de.pbauerochse.worklogviewer.fx.components.statistics.panels.TaskCountByUserAndProjectStatistics
import de.pbauerochse.worklogviewer.fx.components.statistics.panels.TimePerProjectAndUserGraphStatistics
import de.pbauerochse.worklogviewer.fx.components.statistics.panels.TimePerUserAndProjectGraphStatistics
import de.pbauerochse.worklogviewer.report.TimeReport
import de.pbauerochse.worklogviewer.report.view.ReportView
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import de.pbauerochse.worklogviewer.view.grouping.Grouping
import javafx.scene.Node
import org.slf4j.LoggerFactory

/**
 * Tab, that shows all worklogs contained in the
 * [TimeReport], no matter what author or project
 */
internal class AllWorklogsTab : WorklogsTab(getFormatted("view.main.tabs.all")) {

    fun update(report: TimeReport, grouping: Grouping) {
        LOGGER.debug("Showing all worklogs")
        update(text, report.issues, report.reportParameters, grouping)
    }

    override fun getStatistics(reportView: ReportView): List<Node> {
        val dataByUser = TaskCountByUserAndProjectStatisticData(reportView.issues)
        val dataByProject = TaskCountByProjectAndUserStatisticData(reportView.issues)
        return arrayListOf(
            TaskCountByUserAndProjectStatistics(dataByUser),
            TimePerProjectAndUserGraphStatistics(dataByUser),
            TimePerUserAndProjectGraphStatistics(dataByProject)
        )
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(AllWorklogsTab::class.java)
    }
}
