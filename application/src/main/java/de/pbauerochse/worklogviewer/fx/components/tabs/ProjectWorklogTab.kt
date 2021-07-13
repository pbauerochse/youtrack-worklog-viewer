package de.pbauerochse.worklogviewer.fx.components.tabs

import de.pbauerochse.worklogviewer.fx.components.statistics.data.TaskCountByProjectAndUserStatisticData
import de.pbauerochse.worklogviewer.fx.components.statistics.data.TaskCountByUserAndProjectStatisticData
import de.pbauerochse.worklogviewer.fx.components.statistics.panels.TaskCountByUserAndProjectStatistics
import de.pbauerochse.worklogviewer.fx.components.statistics.panels.TimeByGroupingCriteriaChart
import de.pbauerochse.worklogviewer.fx.components.statistics.panels.TimePerProjectAndUserGraphStatistics
import de.pbauerochse.worklogviewer.fx.components.statistics.panels.TimePerUserAndProjectGraphStatistics
import de.pbauerochse.worklogviewer.timereport.view.ReportView
import javafx.scene.Node

/**
 * Tab, that displays the results for a single Project
 * independent of the work author
 */
internal class ProjectWorklogTab : WorklogsTab("") {

    override fun getStatistics(reportView: ReportView): List<Node> {
        val dataByUser = TaskCountByUserAndProjectStatisticData(reportView.issues)
        val dataByProject = TaskCountByProjectAndUserStatisticData(reportView.issues)
        return listOfNotNull(
            reportView.appliedGrouping?.let { TimeByGroupingCriteriaChart(reportView) },
            TaskCountByUserAndProjectStatistics(dataByUser),
            TimePerProjectAndUserGraphStatistics(dataByUser),
            TimePerUserAndProjectGraphStatistics(dataByProject)
        )
    }

}
