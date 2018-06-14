package de.pbauerochse.worklogviewer.fx.components.tabs

import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import de.pbauerochse.worklogviewer.youtrack.TimeReport
import de.pbauerochse.worklogviewer.youtrack.domain.Issue
import de.pbauerochse.worklogviewer.youtrack.domain.Project
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

    private fun extractOwnWorklogs(report: TimeReport): List<Issue> {
        return report.data.projects
            .filter { it.hasTicketsWithOwnWorklogs() }
            .flatMap { getEntriesRelatedToUser(it) }
            .sortedBy { it }
    }

    private fun getEntriesRelatedToUser(it: Project): List<Issue> {
        return it.issues
            .filter { it.hasOwnWorklogs() }
            .map {
                Issue(it).apply {
                    worklogItems.addAll(it.worklogItems.filter { it.isOwn() })
                }
            }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(OwnWorklogsTab::class.java)
        private val LABEL = getFormatted("view.main.tabs.own")
    }

    //    protected List<TaskWithWorklogs> getFilteredList(List<TaskWithWorklogs> tasks) {
    //        Settings settings = SettingsUtil.getSettings();
    //
    //        return tasks.stream()
    //                .filter(taskWithWorklogs -> {
    //                    for (WorklogItem worklogItem : taskWithWorklogs.getWorklogItemList()) {
    //                        if (StringUtils.equals(worklogItem.getUsername(), settings.getYouTrackConnectionSettings().getUsername())) {
    //                            return true;
    //                        }
    //                    }
    //                    return false;
    //                })
    //                .sorted((o1, o2) -> COLLATOR.compare(o1.getIssue(), o2.getIssue()))
    //                .peek(taskWithWorklogs -> {
    //                    // remove items not belonging to self
    //                    taskWithWorklogs
    //                            .getWorklogItemList()
    //                            .removeIf(worklogItem -> !StringUtils.equals(worklogItem.getUsername(), settings.getYouTrackConnectionSettings().getUsername()));
    //                })
    //                .collect(Collectors.toList());
    //    }
    //
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
