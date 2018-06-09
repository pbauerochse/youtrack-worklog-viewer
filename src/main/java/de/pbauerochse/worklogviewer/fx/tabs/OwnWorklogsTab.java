package de.pbauerochse.worklogviewer.fx.tabs;

import de.pbauerochse.worklogviewer.util.FormattingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Patrick Bauerochse
 * @since 02.07.15
 */
public class OwnWorklogsTab extends WorklogsTab {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnWorklogsTab.class);

    OwnWorklogsTab() {
        super(FormattingUtil.getFormatted("view.main.tabs.own"));
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
