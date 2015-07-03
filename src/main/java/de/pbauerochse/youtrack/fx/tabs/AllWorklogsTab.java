package de.pbauerochse.youtrack.fx.tabs;

import de.pbauerochse.youtrack.domain.TaskWithWorklogs;
import de.pbauerochse.youtrack.domain.WorklogItem;
import de.pbauerochse.youtrack.domain.WorklogResult;
import de.pbauerochse.youtrack.util.FormattingUtil;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
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
public class AllWorklogsTab extends WorklogTab {

    private static final Logger LOGGER = LoggerFactory.getLogger(AllWorklogsTab.class);

    private static final int HEIGHT_PER_PROJECT = 50;

    private VBox statisticsView;

    private Optional<List<TaskWithWorklogs>> resultItemsToDisplay = Optional.empty();

    public AllWorklogsTab() {
        super(FormattingUtil.getFormatted("view.main.tabs.all"));
    }

    @Override
    protected Node getStatisticsView() {

        statisticsView = new VBox(20);
        ScrollPane scrollPane = new ScrollPane(statisticsView);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToWidth(true);
        scrollPane.setPadding(new Insets(7));
        return scrollPane;
    }

    @Override
    protected List<TaskWithWorklogs> getDisplayResult(WorklogResult result) {

        if (!resultItemsToDisplay.isPresent() || resultToDisplayChangedSinceLastRender) {
            LOGGER.debug("Extracting TaskWithWorklogs from WorklogResult");

            TaskWithWorklogs summary = new TaskWithWorklogs(true);

            List<TaskWithWorklogs> totalSummary = result.getWorklogSummaryMap()
                    .values().stream()
                    .sorted((o1, o2) -> COLLATOR.compare(o1.getIssue(), o2.getIssue()))
                    .peek(userTaskWorklogs -> {
                        userTaskWorklogs
                                .getWorklogItemList()
                                .stream()
                                .forEach(worklogItem -> summary.getWorklogItemList().add(worklogItem));
                    })
                    .collect(Collectors.toList());

            totalSummary.add(summary);
            resultItemsToDisplay = Optional.of(totalSummary);
        }

        return resultItemsToDisplay.get();
    }

    @Override
    protected void updateStatisticsData() {
        statisticsView.getChildren().clear();

        GridPane gridPane = new GridPane();
        gridPane.setHgap(5);
        gridPane.setVgap(5);

        NumberAxis xAxis = new NumberAxis();
        CategoryAxis yAxis = new CategoryAxis();

        StackedBarChart<Long, String> barChart = new StackedBarChart(xAxis, yAxis);
        VBox.setVgrow(barChart, Priority.ALWAYS);

        barChart.setTitle(FormattingUtil.getFormatted("view.statistics.byprojectandemployee"));

        renderData(gridPane, barChart);

        statisticsView.getChildren().addAll(gridPane, barChart);
    }

    private void renderData(GridPane pane, StackedBarChart barChart) {

        if (resultItemsToDisplay.isPresent()) {
            Map<String, Map<String, AtomicLong>> employeeToProjectToWorktime = new HashMap<>();
            AtomicLong totalTimeSpent = new AtomicLong(0);

            barChart.getXAxis().setLabel(FormattingUtil.getFormatted("view.statistics.timespentinminutes"));
            barChart.getXAxis().setTickLabelRotation(90);

            resultItemsToDisplay.get().forEach(taskWithWorklogs -> {
                if (!taskWithWorklogs.isSummaryRow()) {
                    totalTimeSpent.addAndGet(taskWithWorklogs.getTotalInMinutes());

                    for (WorklogItem worklogItem : taskWithWorklogs.getWorklogItemList()) {

                        Map<String, AtomicLong> projectToTimespent = employeeToProjectToWorktime.get(worklogItem.getUserDisplayname());
                        if (projectToTimespent == null) {
                            projectToTimespent = new HashMap<>();
                            employeeToProjectToWorktime.put(worklogItem.getUserDisplayname(), projectToTimespent);
                        }

                        AtomicLong timespentOnProject = projectToTimespent.get(taskWithWorklogs.getProject());
                        if (timespentOnProject == null) {
                            timespentOnProject = new AtomicLong(0);
                            projectToTimespent.put(taskWithWorklogs.getProject(), timespentOnProject);
                        }

                        timespentOnProject.addAndGet(worklogItem.getDurationInMinutes());
                    }
                }
            });

            final AtomicInteger gridRow = new AtomicInteger(0);
            employeeToProjectToWorktime.keySet().stream()
                    .sorted((o1, o2) -> COLLATOR.compare(o1, o2))
                    .forEach(employee -> {

                        // grid pane
                        Label employeeLabel = getBoldLabel(employee);
                        employeeLabel.setPadding(new Insets(20, 0, 0, 0));
                        GridPane.setConstraints(employeeLabel, 0, gridRow.get());
                        GridPane.setColumnSpan(employeeLabel, 3);
                        pane.getChildren().add(employeeLabel);

                        gridRow.incrementAndGet();

                        // bargraph
                        XYChart.Series series = new XYChart.Series();
                        series.setName(employee);

                        Map<String, AtomicLong> projectToWorktime = employeeToProjectToWorktime.get(employee);
                        projectToWorktime.keySet().stream()
                                .sorted((o1, o2) -> COLLATOR.compare(o1, o2))
                                .forEach(projectName -> {
                                    long timespentInMinutes = projectToWorktime.get(projectName).longValue();

                                    Label projectLabel = getBoldLabel(projectName);
                                    projectLabel.setPadding(new Insets(0, 0, 0, 20));
                                    GridPane.setConstraints(projectLabel, 1, gridRow.get());

                                    Label timespentLabel = new Label(FormattingUtil.formatMinutes(timespentInMinutes, true));
                                    GridPane.setConstraints(timespentLabel, 2, gridRow.get());
                                    GridPane.setHgrow(timespentLabel, Priority.ALWAYS);
                                    GridPane.setHalignment(timespentLabel, HPos.RIGHT);
                                    pane.getChildren().addAll(projectLabel, timespentLabel);

                                    series.getData().add(new XYChart.Data<>(timespentInMinutes, projectName));

                                    gridRow.incrementAndGet();
                                });

                        barChart.getData().add(series);
                    });

            barChart.setPrefHeight(HEIGHT_PER_PROJECT * worklogResult.get().getDistinctProjectNames().size());
        }
    }
}
