package de.pbauerochse.youtrack.fx.tabs;

import de.pbauerochse.youtrack.domain.WorklogItem;
import de.pbauerochse.youtrack.domain.WorklogResult;
import de.pbauerochse.youtrack.util.FormattingUtil;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.text.Format;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Patrick Bauerochse
 * @since 02.07.15
 */
public class AllWorklogsTab extends WorklogTab {

    private VBox statisticsView;

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

        if (resultToDisplay.isPresent()) {
            Map<String, Map<String, AtomicLong>> employeeToProjectToWorktime = new HashMap<>();
            AtomicLong totalTimeSpent = new AtomicLong(0);

            barChart.getXAxis().setLabel(FormattingUtil.getFormatted("view.statistics.timespentinminutes"));
            barChart.getXAxis().setTickLabelRotation(90);

            resultToDisplay.get().forEach(taskWithWorklogs -> {
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
                    .sorted((o1, o2) -> WorklogResult.COLLATOR.compare(o1, o2))
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
                                .sorted((o1, o2) -> WorklogResult.COLLATOR.compare(o1, o2))
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

//            barChart.setPrefHeight(50 * 30); TODO set height by amount of distinct projects
        }
    }
}
