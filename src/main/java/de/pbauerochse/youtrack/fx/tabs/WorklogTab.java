package de.pbauerochse.youtrack.fx.tabs;

import de.pbauerochse.youtrack.WorklogViewer;
import de.pbauerochse.youtrack.domain.*;
import de.pbauerochse.youtrack.excel.ExcelColumnRenderer;
import de.pbauerochse.youtrack.excel.columns.TaskColumn;
import de.pbauerochse.youtrack.excel.columns.TaskSummaryColumn;
import de.pbauerochse.youtrack.excel.columns.WorkdayColumn;
import de.pbauerochse.youtrack.util.ExceptionUtil;
import de.pbauerochse.youtrack.util.FormattingUtil;
import de.pbauerochse.youtrack.util.SettingsUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Collator;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Patrick Bauerochse
 * @since 02.07.15
 */
public abstract class WorklogTab extends Tab {

    protected static final Collator COLLATOR = Collator.getInstance(Locale.GERMANY);

    protected static final String SUMMARY_COLUMN_OR_CELL_CSS_CLASS = "summary";
    protected static final String WEEKEND_COLUMN_OR_CELL_CSS_CLASS = "weekend";
    protected static final String TODAY_COLUMN_OR_CELL_CSS_CLASS = "today";
    protected static final String ISSUE_CELL_CSS_CLASS = "issue-cell";

    // height adjustment parameters for bargraph
    private static final int HEIGHT_PER_PROJECT = 40;
    private static final int HEIGHT_PER_USER = 35;
    private static final int ADDITIONAL_HEIGHT = 150;

    private Logger LOGGER = LoggerFactory.getLogger(WorklogTab.class);

    protected TableView<TaskWithWorklogs> taskTableView;

    protected Optional<ReportTimerange> lastUsedTimerange = Optional.empty();

    protected Optional<WorklogResult> worklogResult = Optional.empty();

    protected boolean resultToDisplayChangedSinceLastRender;

    protected VBox statisticsView;

    /**
     * Extract the appropriate TaskWithWorklogs from the WorklogResult item
     * @param result
     * @return
     */
    protected abstract List<TaskWithWorklogs> getDisplayResult(WorklogResult result);

    public WorklogTab(String name) {
        super(name);

        setContent(getContentNode());

        // when this tab becomes active render the data
        selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.booleanValue() && resultToDisplayChangedSinceLastRender) {
                // tab switched to active
                LOGGER.debug("Showing tab {}", getText());
                refreshWorklogTableViewAndResults();
            }
        });
    }

    /**
     * Update the items to display. If this tab is currently selected
     * the content will be refreshed. Else it will be refreshed whenever
     * this tab becomes active
     *
     * @param worklogList The TaskWithWorklogs to show in this tab
     */
    public void updateItems(WorklogResult worklogResult) {
        this.worklogResult = Optional.of(worklogResult);
        resultToDisplayChangedSinceLastRender = true;

        if (isSelected()) {
            refreshWorklogTableViewAndResults();
        }
    }

    protected Node getContentNode() {
        Node taskView = getTaskView();

        if (SettingsUtil.loadSettings().isShowStatistics()) {
            LOGGER.debug("Statistics enabled in settings");
            statisticsView = new VBox(20);

            // wrap statistics in scrollpane
            ScrollPane scrollPane = new ScrollPane(statisticsView);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setFitToWidth(true);
            scrollPane.setPadding(new Insets(7));

            // both statistics and task view present
            // show both in a split pane
            SplitPane splitPane = new SplitPane();
            splitPane.setOrientation(Orientation.HORIZONTAL);
            splitPane.setDividerPosition(0, 0.8);
            splitPane.getItems().addAll(taskView, scrollPane);
            return splitPane;
        } else {
            LOGGER.debug("Statistics disabled in settings");
            return taskView;
        }
    }

    protected Node getTaskView() {

        if (taskTableView == null) {
            taskTableView = new TableView<>();
        }

        AnchorPane anchorPane = new AnchorPane(taskTableView);
        anchorPane.setPadding(new Insets(6));

        AnchorPane.setTopAnchor(taskTableView, 0d);
        AnchorPane.setRightAnchor(taskTableView, 0d);
        AnchorPane.setBottomAnchor(taskTableView, 0d);
        AnchorPane.setLeftAnchor(taskTableView, 0d);
        return anchorPane;
    }

    protected void refreshWorklogTableViewAndResults() {

        // return early if no data present or still the same data
        // as the last time this tab was active
        if (!worklogResult.isPresent() || !resultToDisplayChangedSinceLastRender) {
            LOGGER.debug("[{}] No results to display or data not changed. Not refreshing TableView and data", getText());
            return;
        }

        SettingsUtil.Settings settings = SettingsUtil.loadSettings();
        if (settings.isShowStatistics() && statisticsView == null || !settings.isShowStatistics() && statisticsView != null) {
            // statistics are disabled and were previously rendered
            // or statistics are enabled and weren't rendered before
            // update content view
            LOGGER.debug("Updating contentView since settings for statistics seemed to have changed");
            setContent(getContentNode());
        }

        // render the table columns if the timerange changed from last result
        WorklogResult worklogResult = this.worklogResult.get();
        if (!lastUsedTimerange.isPresent() || lastUsedTimerange.get() != worklogResult.getTimerange()) {

            LOGGER.debug("[{}] Regenerating columns for timerange {}", getText(), worklogResult.getTimerange().name());
            taskTableView.getColumns().clear();
            taskTableView.getColumns().add(getDescriptionColumn());

            // render tables for all days in the selected timerange
            // e.g. timerange current month renders a column for
            // each day of the current month
            TimerangeProvider timerangeProvider = worklogResult.getTimerange().getTimerangeProvider();
            long amountOfDaysToDisplay = ChronoUnit.DAYS.between(timerangeProvider.getStartDate(), timerangeProvider.getEndDate());

            for (int days = 0; days <= amountOfDaysToDisplay; days++) {
                LocalDate currentColumnDate = timerangeProvider.getStartDate().plus(days, ChronoUnit.DAYS);
                DayOfWeek currentColumnDayOfWeek = currentColumnDate.getDayOfWeek();
                String displayDate = FormattingUtil.formatDate(currentColumnDate);

                // create column
                TableColumn<TaskWithWorklogs, TaskWithWorklogs> column = new TableColumn<>(displayDate);
                column.setSortable(false);
                column.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue()));
                column.setCellFactory(param -> {
                    TableCell<TaskWithWorklogs, TaskWithWorklogs> cell = new TableCell<TaskWithWorklogs, TaskWithWorklogs>() {
                        @Override
                        protected void updateItem(TaskWithWorklogs item, boolean empty) {
                            super.updateItem(item, empty);

                            if (empty) {
                                // clear cell and tooltip
                                setText(StringUtils.EMPTY);
                                setTooltip(null);
                                getStyleClass().remove(SUMMARY_COLUMN_OR_CELL_CSS_CLASS);
                            } else {
                                // display the spent time as cell value
                                // and the date with the spent time as tooltip
                                String worklogTimeFormatted = FormattingUtil.formatMinutes(item.getTotalInMinutes(currentColumnDate));

                                setText(worklogTimeFormatted);
                                setTooltip(new Tooltip(displayDate + " - " + worklogTimeFormatted));

                                if (item.isSummaryRow()) {
                                    getStyleClass().add(SUMMARY_COLUMN_OR_CELL_CSS_CLASS);
                                }
                            }
                        }
                    };

                    cell.setAlignment(Pos.CENTER_RIGHT);

                    return cell;
                });

                // add class for weekend columns
                if (currentColumnDayOfWeek == DayOfWeek.SATURDAY || currentColumnDayOfWeek == DayOfWeek.SUNDAY) {
                    column.getStyleClass().add(WEEKEND_COLUMN_OR_CELL_CSS_CLASS);
                    column.setPrefWidth(20);
                } else {
                    column.setPrefWidth(100);
                }

                // today
                if (currentColumnDate.isEqual(LocalDate.now())) {
                    column.getStyleClass().add(TODAY_COLUMN_OR_CELL_CSS_CLASS);
                }

                taskTableView.getColumns().add(column);
            }

            // also add another summary per task column
            TableColumn<TaskWithWorklogs, String> summaryPerTaskColumn = new TableColumn<>(FormattingUtil.getFormatted("view.main.summary"));
            summaryPerTaskColumn.setSortable(false);
            summaryPerTaskColumn.setCellValueFactory(param -> new SimpleStringProperty(FormattingUtil.formatMinutes(param.getValue().getTotalInMinutes())));
            summaryPerTaskColumn.setCellFactory(param -> {
                TableCell<TaskWithWorklogs, String> summaryCell = new TableCell<TaskWithWorklogs, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setText(StringUtils.defaultIfBlank(item, StringUtils.EMPTY));
                    }
                };
                summaryCell.getStyleClass().add(SUMMARY_COLUMN_OR_CELL_CSS_CLASS);
                summaryCell.setAlignment(Pos.CENTER_RIGHT);
                return summaryCell;
            });
            summaryPerTaskColumn.setPrefWidth(120);
            taskTableView.getColumns().add(summaryPerTaskColumn);

            lastUsedTimerange = Optional.of(worklogResult.getTimerange());
        }

        // only refresh items if changed from last time
        if (resultToDisplayChangedSinceLastRender) {
            LOGGER.debug("[{}] Refreshing items in TableView", getText());
            taskTableView.getItems().clear();


            List<TaskWithWorklogs> displayResult = getDisplayResult(worklogResult);
            if (displayResult == null) {
                LOGGER.warn("[{}] returned a null resultset", getText());
                displayResult = new ArrayList<>();
            }

            taskTableView.getItems().addAll(displayResult);

            updateStatisticsData(displayResult);

            resultToDisplayChangedSinceLastRender = false;
        }
    }

    private void updateStatisticsData(List<TaskWithWorklogs> displayResult) {

        if (!SettingsUtil.loadSettings().isShowStatistics()) {
            return;
        }

        statisticsView.getChildren().clear();

        WorklogStatistics statistics = new WorklogStatistics();

        // generic statistics
        displayResult.forEach(taskWithWorklogs -> {
            if (!taskWithWorklogs.isSummaryRow()) {
                statistics.getTotalTimeSpent().addAndGet(taskWithWorklogs.getTotalInMinutes());

                for (WorklogItem worklogItem : taskWithWorklogs.getWorklogItemList()) {
                    String employee = worklogItem.getUserDisplayname();

                    // employee total time spent
                    AtomicLong totalTimeSpent = statistics.getEmployeeToTotaltimeSpent().get(employee);
                    if (totalTimeSpent == null) {
                        totalTimeSpent = new AtomicLong(0);
                        statistics.getEmployeeToTotaltimeSpent().put(employee, totalTimeSpent);
                    }
                    totalTimeSpent.addAndGet(worklogItem.getDurationInMinutes());

                    // distinct tasks per employee
                    Set<String> totalDistinctTasks = statistics.getEmployeeToTotalDistinctTasks().get(employee);
                    if (totalDistinctTasks == null) {
                        totalDistinctTasks = new HashSet<>();
                        statistics.getEmployeeToTotalDistinctTasks().put(employee, totalDistinctTasks);
                    }
                    totalDistinctTasks.add(taskWithWorklogs.getIssue());

                    // distinct tasks per employee per project
                    Map<String, Set<String>> projectToDistinctTasks = statistics.getEmployeeToProjectToDistinctTasks().get(employee);
                    if (projectToDistinctTasks == null) {
                        projectToDistinctTasks = new HashMap<>();
                        statistics.getEmployeeToProjectToDistinctTasks().put(employee, projectToDistinctTasks);
                    }

                    Set<String> distinctTasks = projectToDistinctTasks.get(taskWithWorklogs.getProject());
                    if (distinctTasks == null) {
                        distinctTasks = new HashSet<>();
                        projectToDistinctTasks.put(taskWithWorklogs.getProject(), distinctTasks);
                    }

                    distinctTasks.add(taskWithWorklogs.getIssue());

                    // time spent per project
                    Map<String, AtomicLong> projectToTimespent = statistics.getEmployeeToProjectToWorktime().get(employee);
                    if (projectToTimespent == null) {
                        projectToTimespent = new HashMap<>();
                        statistics.getEmployeeToProjectToWorktime().put(employee, projectToTimespent);
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

        // render grid and bar graph
        final AtomicInteger currentGridRow = new AtomicInteger(0);

        GridPane employeeProjectSummaryGrid = new GridPane();
        employeeProjectSummaryGrid.setHgap(5);
        employeeProjectSummaryGrid.setVgap(5);

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel(FormattingUtil.getFormatted("view.statistics.timespentinminutes"));
        xAxis.setTickLabelRotation(90);

        CategoryAxis yAxis = new CategoryAxis();

        StackedBarChart<Number, String> projectEmployeeBargraph = new StackedBarChart<>(xAxis, yAxis);
        projectEmployeeBargraph.setTitle(FormattingUtil.getFormatted("view.statistics.byprojectandemployee"));

        Set<String> projectsToDisplay = new HashSet<>();
        displayResult.forEach(taskWithWorklogs -> {
            if (!taskWithWorklogs.isSummaryRow()) {
                projectsToDisplay.add(taskWithWorklogs.getProject());
            }
        });
        int prefHeight = HEIGHT_PER_PROJECT * projectsToDisplay.size() + HEIGHT_PER_USER * statistics.getEmployeeToTotaltimeSpent().keySet().size() + ADDITIONAL_HEIGHT;
        LOGGER.debug("Setting bargraph height to {} * {} + {} * {} + {} = {}", HEIGHT_PER_PROJECT, projectsToDisplay.size(), HEIGHT_PER_USER, statistics.getEmployeeToTotaltimeSpent().keySet().size(), ADDITIONAL_HEIGHT, prefHeight);

        projectEmployeeBargraph.setPrefHeight(prefHeight);
        VBox.setVgrow(projectEmployeeBargraph, Priority.ALWAYS);

        statistics.getEmployeeToProjectToWorktime().keySet().stream()
                .sorted(COLLATOR::compare)
                .forEach(employee -> {

                    // employee headline label
                    Set<String> totalDistinctTasksOfEmployee = statistics.getEmployeeToTotalDistinctTasks().get(employee);
                    Label employeeLabel = getBoldLabel(FormattingUtil.getFormatted("view.statistics.somethingtoamountoftickets", employee, totalDistinctTasksOfEmployee.size()));
                    employeeLabel.setPadding(new Insets(20, 0, 0, 0));
                    GridPane.setConstraints(employeeLabel, 0, currentGridRow.getAndIncrement());
                    GridPane.setColumnSpan(employeeLabel, 3);
                    employeeProjectSummaryGrid.getChildren().addAll(employeeLabel);

                    // bar graph data container
                    XYChart.Series<Number, String> series = new XYChart.Series<>();
                    series.setName(employee);
                    projectEmployeeBargraph.getData().add(series);

                    // time spent per project
                    Map<String, AtomicLong> projectToWorktime = statistics.getEmployeeToProjectToWorktime().get(employee);
                    projectToWorktime.keySet().stream()
                            .sorted(COLLATOR::compare)
                            .forEach(projectName -> {

                                // project label
                                Set<String> distinctTasksPerProject = statistics.getEmployeeToProjectToDistinctTasks().get(employee).get(projectName);
                                Label projectLabel = getBoldLabel(FormattingUtil.getFormatted("view.statistics.somethingtoamountoftickets", projectName, distinctTasksPerProject.size()));
                                projectLabel.setPadding(new Insets(0, 0, 0, 20));
                                GridPane.setConstraints(projectLabel, 1, currentGridRow.get());

                                // time spent for project label
                                long timespentInMinutes = projectToWorktime.get(projectName).longValue();
                                Label timespentLabel = new Label(FormattingUtil.formatMinutes(timespentInMinutes, true));
                                GridPane.setConstraints(timespentLabel, 2, currentGridRow.get());
                                GridPane.setHgrow(timespentLabel, Priority.ALWAYS);
                                GridPane.setHalignment(timespentLabel, HPos.RIGHT);

                                employeeProjectSummaryGrid.getChildren().addAll(projectLabel, timespentLabel);
                                currentGridRow.incrementAndGet();

                                // bargraph data
                                series.getData().add(new XYChart.Data<>(timespentInMinutes, projectName));
                            });

                    // total time spent
                    Label totalLabel = getBoldLabel(FormattingUtil.getFormatted("view.statistics.totaltimespent"));
                    GridPane.setConstraints(totalLabel, 0, currentGridRow.get());
                    GridPane.setColumnSpan(totalLabel, 3);

                    Label timespentLabel = new Label(FormattingUtil.formatMinutes(statistics.getEmployeeToTotaltimeSpent().get(employee).get(), true));
                    GridPane.setConstraints(timespentLabel, 2, currentGridRow.get());
                    GridPane.setHgrow(timespentLabel, Priority.ALWAYS);
                    GridPane.setHalignment(timespentLabel, HPos.RIGHT);
                    employeeProjectSummaryGrid.getChildren().addAll(totalLabel, timespentLabel);

                    currentGridRow.incrementAndGet();
                });
        statisticsView.getChildren().addAll(employeeProjectSummaryGrid, projectEmployeeBargraph);

        // custom view statistics
        addAdditionalStatistics(statisticsView, statistics, displayResult);
    }

    protected void addAdditionalStatistics(VBox statisticsView, WorklogStatistics statistics, List<TaskWithWorklogs> displayResult) {
        // for you to override
    }

    private Optional<TableColumn<TaskWithWorklogs, TaskWithWorklogs>> descriptionColumnOptional = Optional.empty();
    protected TableColumn<TaskWithWorklogs, TaskWithWorklogs> getDescriptionColumn() {

        if (!descriptionColumnOptional.isPresent()) {

            LOGGER.debug("[{}] Generating description column", getText());

            TableColumn<TaskWithWorklogs, TaskWithWorklogs> descriptionColumn = new TableColumn<>(FormattingUtil.getFormatted("view.main.issue"));
            descriptionColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue()));
            descriptionColumn.setCellFactory(param -> {
                TableCell<TaskWithWorklogs, TaskWithWorklogs> tableCell = new TableCell<TaskWithWorklogs, TaskWithWorklogs>() {

                    @Override
                    protected void updateItem(TaskWithWorklogs item, boolean empty) {
                        if (empty) {
                            setText(StringUtils.EMPTY);
                            setTooltip(null);
                        } else {
                            if (item.isSummaryRow()) {
                                setText(FormattingUtil.getFormatted("view.main.summary"));
                                setTooltip(null);
                                getStyleClass().add(SUMMARY_COLUMN_OR_CELL_CSS_CLASS);
                                getStyleClass().remove(ISSUE_CELL_CSS_CLASS);
                                setAlignment(Pos.CENTER_RIGHT);
                            } else {
                                setText(item.getIssue() + " - " + item.getSummary());
                                setTooltip(new Tooltip(getText()));
                                getStyleClass().remove(SUMMARY_COLUMN_OR_CELL_CSS_CLASS);
                                getStyleClass().add(ISSUE_CELL_CSS_CLASS);
                                setAlignment(Pos.CENTER_LEFT);
                            }
                        }
                    }
                };

                tableCell.setOnMouseClicked(event -> {
                    TableCell<TaskWithWorklogs, TaskWithWorklogs> cell = (TableCell<TaskWithWorklogs, TaskWithWorklogs>) event.getSource();
                    int index = cell.getIndex();

                    if (index < taskTableView.getItems().size()) {
                        TaskWithWorklogs clickedWorklogItem = taskTableView.getItems().get(index);
                        if (!clickedWorklogItem.isSummaryRow()) {
                            SettingsUtil.Settings settings = SettingsUtil.loadSettings();
                            String issueUrl = String.format("%s/issue/%s#tab=Time%%20Tracking", StringUtils.stripEnd(settings.getYoutrackUrl(), "/"), clickedWorklogItem.getIssue());
                            Platform.runLater(() -> WorklogViewer.getInstance().getHostServices().showDocument(issueUrl));
                        }
                    }
                });

                return tableCell;
            });

            descriptionColumn.setPrefWidth(300);
            descriptionColumn.setMinWidth(300);

            descriptionColumnOptional = Optional.of(descriptionColumn);
        }

        return descriptionColumnOptional.get();
    }

    protected Label getBoldLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, Font.getDefault().getSize()));
        return label;
    }

    public String getExcelDownloadSuggestedFilename() {
        if (!lastUsedTimerange.isPresent()) throw ExceptionUtil.getIllegalStateException("exceptions.excel.nodata");

        ReportTimerange reportTimerange = lastUsedTimerange.get();
        TimerangeProvider timerangeProvider = reportTimerange.getTimerangeProvider();

        return new StringBuilder(getText())
                .append('_')
                .append(FormattingUtil.formatDate(timerangeProvider.getStartDate()))
                .append('-')
                .append(FormattingUtil.formatDate(timerangeProvider.getEndDate()))
                .append(".xls")
                .toString();
    }

    public void writeDataToExcel(Sheet sheet) {
        LOGGER.debug("[{}] Exporting data to excel", getText());

        List<ExcelColumnRenderer> columnRendererList = new ArrayList<>();
        columnRendererList.add(new TaskColumn());

        TimerangeProvider timerangeProvider = worklogResult.get().getTimerange().getTimerangeProvider();
        LocalDate startDate = timerangeProvider.getStartDate();
        LocalDate endDate = timerangeProvider.getEndDate();

        long amountOfDaysToDisplay = ChronoUnit.DAYS.between(startDate, endDate);
        for (int days = 0; days <= amountOfDaysToDisplay; days++) {
            LocalDate currentColumnDate = timerangeProvider.getStartDate().plus(days, ChronoUnit.DAYS);
            DayOfWeek currentColumnDayOfWeek = currentColumnDate.getDayOfWeek();
            String displayDate = FormattingUtil.formatDate(currentColumnDate);
            columnRendererList.add(new WorkdayColumn(displayDate, currentColumnDate));
        }

        columnRendererList.add(new TaskSummaryColumn());
        columnRendererList.forEach(excelColumnRenderer -> excelColumnRenderer.renderCells(columnRendererList.indexOf(excelColumnRenderer), sheet, worklogResult.get(), getDisplayResult(worklogResult.get())));

        // autosize column widths
        for (int i = 0; i < columnRendererList.size(); i++) {
            sheet.autoSizeColumn(i);
        }
    }

    protected class WorklogStatistics {

        private Map<String, Map<String, AtomicLong>> employeeToProjectToWorktime = new HashMap<>();
        private Map<String, Map<String, Set<String>>> employeeToProjectToDistinctTasks = new HashMap<>();
        private Map<String, Set<String>> employeeToTotalDistinctTasks = new HashMap<>();
        private AtomicLong totalTimeSpent = new AtomicLong(0);
        private Map<String, AtomicLong> employeeToTotaltimeSpent = new HashMap<>();

        public Map<String, Map<String, AtomicLong>> getEmployeeToProjectToWorktime() {
            return employeeToProjectToWorktime;
        }

        public Map<String, Map<String, Set<String>>> getEmployeeToProjectToDistinctTasks() {
            return employeeToProjectToDistinctTasks;
        }

        public Map<String, Set<String>> getEmployeeToTotalDistinctTasks() {
            return employeeToTotalDistinctTasks;
        }

        public AtomicLong getTotalTimeSpent() {
            return totalTimeSpent;
        }

        public Map<String, AtomicLong> getEmployeeToTotaltimeSpent() {
            return employeeToTotaltimeSpent;
        }
    }

}
