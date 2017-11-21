package de.pbauerochse.worklogviewer.fx.tabs;

import de.pbauerochse.worklogviewer.settings.Settings;
import de.pbauerochse.worklogviewer.settings.SettingsUtil;
import de.pbauerochse.worklogviewer.util.FormattingUtil;
import de.pbauerochse.worklogviewer.youtrack.domain.TaskWithWorklogs;
import de.pbauerochse.worklogviewer.youtrack.domain.WorklogItem;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author Patrick Bauerochse
 * @since 02.07.15
 */
public class OwnWorklogsTab extends WorklogTab {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnWorklogsTab.class);

    public OwnWorklogsTab() {
        super(FormattingUtil.getFormatted("view.main.tabs.own"));
    }

    @Override
    protected List<TaskWithWorklogs> getFilteredList(List<TaskWithWorklogs> tasks) {
        Settings settings = SettingsUtil.getSettings();

        return tasks.stream()
                .filter(taskWithWorklogs -> {
                    for (WorklogItem worklogItem : taskWithWorklogs.getWorklogItemList()) {
                        if (StringUtils.equals(worklogItem.getUsername(), settings.getYoutrackUsername())) {
                            return true;
                        }
                    }
                    return false;
                })
                .sorted((o1, o2) -> COLLATOR.compare(o1.getIssue(), o2.getIssue()))
                .peek(taskWithWorklogs -> {
                    // remove items not belonging to self
                    taskWithWorklogs
                            .getWorklogItemList()
                            .removeIf(worklogItem -> !StringUtils.equals(worklogItem.getUsername(), settings.getYoutrackUsername()));
                })
                .collect(Collectors.toList());
    }

    @Override
    protected void addAdditionalStatistics(VBox statisticsView, WorklogStatistics statistics, List<TaskWithWorklogs> displayResult) {
        super.addAdditionalStatistics(statisticsView, statistics, displayResult);

        if (statistics.getEmployeeToProjectToWorktime().size() > 0) {
            PieChart pieChart = new PieChart();
            pieChart.setLabelsVisible(false);
            pieChart.setTitle(FormattingUtil.getFormatted("view.statistics.byproject"));
            statisticsView.getChildren().add(pieChart);

            // since getDisplayData only returns own data
            // we can safely assume the employee map only contains
            // one username
            Map.Entry<String, Map<String, AtomicLong>> projectToWorktime = statistics.getEmployeeToProjectToWorktime().entrySet().iterator().next();
            projectToWorktime.getValue().keySet().stream()
                    .sorted(COLLATOR::compare)
                    .forEach(project -> {
                        AtomicLong timespentForThisProject = projectToWorktime.getValue().get(project);
                        pieChart.getData().add(new PieChart.Data(project, timespentForThisProject.doubleValue()));
                    });
        }
    }
}
