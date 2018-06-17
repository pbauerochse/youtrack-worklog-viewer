package de.pbauerochse.worklogviewer.fx.components.tabs

import de.pbauerochse.worklogviewer.fx.components.statistics.TaskCountByUserAndProjectStatisticData
import de.pbauerochse.worklogviewer.fx.components.statistics.TaskCountByUserAndProjectStatistics
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import de.pbauerochse.worklogviewer.youtrack.TimeReport
import de.pbauerochse.worklogviewer.youtrack.domain.Issue
import de.pbauerochse.worklogviewer.youtrack.domain.Project
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
        val data = TaskCountByUserAndProjectStatisticData(issues)

        return arrayListOf(
            TaskCountByUserAndProjectStatistics(data)
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

    //    @Override
    //    protected void addAdditionalStatistics(VBox statisticsView, WorklogStatistics statistics, List<TaskWithWorklogs> displayResult) {
    //        super.addAdditionalStatistics(statisticsView, statistics, displayResult);
    //
    //        if (statistics.getEmployeeToProjectToWorktime().size() > 0) {
    //            PieChart pieChart = new PieChart();
    //            pieChart.setLabelsVisible(false);
    //            pieChart.setTitle(FormattingUtil.getFormatted("view.statistics.byproject"));
    //            statisticsView.getChildren().add(pieChart);
    //
    //            // since getDisplayData only returns own data
    //            // we can safely assume the employee map only contains
    //            // one username
    //            Map.Entry<String, Map<String, AtomicLong>> projectToWorktime = statistics.getEmployeeToProjectToWorktime().entrySet().iterator().next();
    //            projectToWorktime.getValue().keySet().stream()
    //                    .sorted(COLLATOR::compare)
    //                    .forEach(project -> {
    //                        AtomicLong timespentForThisProject = projectToWorktime.getValue().get(project);
    //                        pieChart.getData().add(new PieChart.Data(project, timespentForThisProject.doubleValue()));
    //                    });
    //        }
    //    }
}
