package de.pbauerochse.youtrack.fx.tabs;

import de.pbauerochse.youtrack.domain.TaskWithWorklogs;
import de.pbauerochse.youtrack.domain.WorklogItem;
import de.pbauerochse.youtrack.domain.WorklogResult;
import de.pbauerochse.youtrack.util.FormattingUtil;
import de.pbauerochse.youtrack.util.SettingsUtil;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author Patrick Bauerochse
 * @since 02.07.15
 */
public class OwnWorklogsTab extends WorklogTab {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnWorklogsTab.class);

    private Optional<List<TaskWithWorklogs>> resultItemsToDisplay = Optional.empty();

    public OwnWorklogsTab() {
        super(FormattingUtil.getFormatted("view.main.tabs.own"));
    }

    @Override
    protected List<TaskWithWorklogs> getDisplayResult(WorklogResult result) {

        if (!resultItemsToDisplay.isPresent() || resultToDisplayChangedSinceLastRender) {
            LOGGER.debug("Extracting TaskWithWorklogs from WorklogResult");
            String youtrackUsername = SettingsUtil.loadSettings().getYoutrackUsername();

            TaskWithWorklogs summary = new TaskWithWorklogs(true);

            List<TaskWithWorklogs> ownSummary = result.getWorklogSummaryMap()
                    .values().stream()
                    .filter(taskWithWorklogs -> {
                        // only those entries where the username matches
                        for (WorklogItem worklogItem : taskWithWorklogs.getWorklogItemList()) {

                            if (StringUtils.equals(worklogItem.getUsername(), youtrackUsername)) {
                                return true;
                            }
                        }

                        return false;
                    })
                    .map(taskWithWorklogs -> taskWithWorklogs.createCopy())
                    .sorted((o1, o2) -> COLLATOR.compare(o1.getIssue(), o2.getIssue()))
                    .peek(taskWithWorklogs -> taskWithWorklogs
                            .getWorklogItemList()
                            .stream()
                            .forEach(worklogItem -> {
                                if (StringUtils.equals(worklogItem.getUsername(), youtrackUsername)) {
                                    summary.getWorklogItemList().add(worklogItem);
                                }
                            }))
                    .collect(Collectors.toList());

            ownSummary.forEach(taskWithWorklogs -> {

                if (!taskWithWorklogs.isSummaryRow()) {

                    for (Iterator<WorklogItem> iterator = taskWithWorklogs.getWorklogItemList().iterator(); iterator.hasNext(); ) {
                        WorklogItem worklogItem = iterator.next();
                        if (!StringUtils.equals(worklogItem.getUsername(), youtrackUsername)) {
                            iterator.remove();
                        }
                    }
                }
            });

            ownSummary.add(summary);

            resultItemsToDisplay = Optional.of(ownSummary);
        }

        return resultItemsToDisplay.get();
    }

    @Override
    protected void addAdditionalStatistics(VBox statisticsView, WorklogStatistics statistics, List<TaskWithWorklogs> displayResult) {
        super.addAdditionalStatistics(statisticsView, statistics, displayResult);

        PieChart pieChart = new PieChart();
        pieChart.setLabelsVisible(false);
        pieChart.setTitle(FormattingUtil.getFormatted("view.statistics.byproject"));
        statisticsView.getChildren().add(pieChart);

        // since getDisplayResult only returns own data
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
