package de.pbauerochse.youtrack.fx.tabs;

import de.pbauerochse.youtrack.domain.TaskWithWorklogs;
import de.pbauerochse.youtrack.domain.WorklogItem;
import de.pbauerochse.youtrack.domain.WorklogResult;
import de.pbauerochse.youtrack.util.FormattingUtil;
import de.pbauerochse.youtrack.util.SettingsUtil;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author Patrick Bauerochse
 * @since 02.07.15
 */
public class OwnWorklogsTab extends WorklogTab {

    private static final Logger LOGGER = LoggerFactory.getLogger(OwnWorklogsTab.class);

    private VBox statisticsView;

    private Optional<List<TaskWithWorklogs>> resultItemsToDisplay = Optional.empty();

    public OwnWorklogsTab() {
        super(FormattingUtil.getFormatted("view.main.tabs.own"));
    }

    @Override
    protected Node getStatisticsView() {
        statisticsView = new VBox(20);
        statisticsView.setPadding(new Insets(20, 5, 5, 5));
        return statisticsView;
    }

    @Override
    protected void updateStatisticsData() {
        statisticsView.getChildren().clear();

        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        PieChart pieChart = new PieChart();
        pieChart.setLabelsVisible(false);
        pieChart.setTitle(FormattingUtil.getFormatted("view.statistics.byproject"));

        renderData(gridPane, pieChart);

        statisticsView.getChildren().addAll(gridPane, pieChart);
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

    private void renderData(GridPane pane, PieChart chart) {

        if (resultItemsToDisplay.isPresent()) {
            Map<String, AtomicLong> projectToTimespent = new HashMap<>();
            AtomicLong totalTimeSpent = new AtomicLong(0);

            resultItemsToDisplay.get().forEach(taskWithWorklogs -> {

                if (!taskWithWorklogs.isSummaryRow()) {
                    AtomicLong timeSpentInMinutes = projectToTimespent.get(taskWithWorklogs.getProject());
                    if (timeSpentInMinutes == null) {
                        timeSpentInMinutes = new AtomicLong(0);
                        projectToTimespent.put(taskWithWorklogs.getProject(), timeSpentInMinutes);
                    }

                    timeSpentInMinutes.addAndGet(taskWithWorklogs.getTotalInMinutes());
                    totalTimeSpent.addAndGet(taskWithWorklogs.getTotalInMinutes());
                }
            });

            final AtomicInteger currentRow = new AtomicInteger(0);

            // add labels and chart data
            projectToTimespent.keySet().stream()
                    .sorted((o1, o2) -> COLLATOR.compare(o1, o2))
                    .forEach(project -> {
                        AtomicLong timespentForThisProject = projectToTimespent.get(project);

                        // add grid labels
                        Label label = getBoldLabel(project);
                        GridPane.setConstraints(label, 0, currentRow.get());

                        Label value = new Label(FormattingUtil.formatMinutes(timespentForThisProject.longValue(), true));
                        GridPane.setConstraints(value, 1, currentRow.get());
                        GridPane.setHgrow(value, Priority.ALWAYS);
                        GridPane.setHalignment(value, HPos.RIGHT);

                        pane.getChildren().addAll(label, value);

                        // add chart data
                        chart.getData().add(new PieChart.Data(project, timespentForThisProject.doubleValue()));
                        currentRow.incrementAndGet();
                    });

            Label totalTimeSpentLabel = getBoldLabel(FormattingUtil.getFormatted("view.statistics.totaltimespent"));
            GridPane.setConstraints(totalTimeSpentLabel, 0, currentRow.get());

            Label totalTimeSpentValue = getBoldLabel(FormattingUtil.formatMinutes(totalTimeSpent.longValue(), true));
            GridPane.setConstraints(totalTimeSpentValue, 1, currentRow.get());
            GridPane.setHgrow(totalTimeSpentValue, Priority.ALWAYS);
            GridPane.setHalignment(totalTimeSpentValue, HPos.RIGHT);

            pane.getChildren().addAll(totalTimeSpentLabel, totalTimeSpentValue);
        }
    }

}
