package de.pbauerochse.youtrack.fx.tabs;

import de.pbauerochse.youtrack.domain.TaskWithWorklogs;
import de.pbauerochse.youtrack.domain.TimerangeProvider;
import de.pbauerochse.youtrack.domain.WorklogItem;
import de.pbauerochse.youtrack.domain.WorklogResult;
import de.pbauerochse.youtrack.excel.ExcelColumnRenderer;
import de.pbauerochse.youtrack.excel.columns.TaskDescriptionExcelColumn;
import de.pbauerochse.youtrack.excel.columns.TaskWorklogSummaryExcelColumn;
import de.pbauerochse.youtrack.excel.columns.WorklogExcelColumn;
import de.pbauerochse.youtrack.fx.tablecolumns.TaskDescriptionTreeTableColumn;
import de.pbauerochse.youtrack.fx.tablecolumns.TaskWorklogSummaryTreeTableColumn;
import de.pbauerochse.youtrack.fx.tablecolumns.WorklogTreeTableColumn;
import de.pbauerochse.youtrack.fx.tasks.FetchTimereportContext;
import de.pbauerochse.youtrack.util.ExceptionUtil;
import de.pbauerochse.youtrack.util.FormattingUtil;
import de.pbauerochse.youtrack.util.SettingsUtil;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
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

    // height adjustment parameters for bargraph
    private static final int HEIGHT_PER_PROJECT = 40;
    private static final int HEIGHT_PER_USER = 35;
    private static final int ADDITIONAL_HEIGHT = 150;

    private Logger LOGGER = LoggerFactory.getLogger(WorklogTab.class);

    protected TreeTableView<TaskWithWorklogs> taskTableView;

    protected Optional<TimerangeProvider> lastUsedTimerangeProvider = Optional.empty();

    protected Optional<WorklogResult> worklogResult = Optional.empty();

    protected Optional<FetchTimereportContext> fetchTimereportContext = Optional.empty();

    protected boolean resultToDisplayChangedSinceLastRender;

    protected VBox statisticsView;
    private TaskDescriptionTreeTableColumn taskDescriptionTreeTableColumn;

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
    public void updateItems(WorklogResult worklogResult, FetchTimereportContext timereportContext) {
        this.worklogResult = Optional.of(worklogResult);
        this.fetchTimereportContext = Optional.of(timereportContext);

        resultToDisplayChangedSinceLastRender = true;

        if (isSelected()) {
            refreshWorklogTableViewAndResults();
        }
    }

    protected Node getContentNode() {
        Node taskView = getTaskView();

        if (SettingsUtil.loadSettings().getShowStatistics()) {
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
            taskTableView = new TreeTableView<>(new TreeItem<>());
            taskTableView.setShowRoot(false);
        }

        if (taskDescriptionTreeTableColumn == null) {
            taskDescriptionTreeTableColumn = new TaskDescriptionTreeTableColumn();
            taskDescriptionTreeTableColumn.setPrefWidth(300);
            taskDescriptionTreeTableColumn.setMinWidth(300);
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
        if (settings.getShowStatistics() && statisticsView == null || !settings.getShowStatistics() && statisticsView != null) {
            // statistics are disabled and were previously rendered
            // or statistics are enabled and weren't rendered before
            // update content view
            LOGGER.debug("Updating contentView since settings for statistics seemed to have changed");
            setContent(getContentNode());
        }

        // render the table columns if the timerange changed from last result
        WorklogResult worklogResult = this.worklogResult.get();
        if (!lastUsedTimerangeProvider.isPresent() || lastUsedTimerangeProvider.get().getReportTimerange() != worklogResult.getTimerange()) {

            LOGGER.debug("[{}] Regenerating columns for timerange {}", getText(), worklogResult.getTimerange().name());
            taskTableView.getColumns().clear();
            taskTableView.getColumns().add(taskDescriptionTreeTableColumn);

            // render tables for all days in the selected timerange
            // e.g. timerange current month renders a column for
            // each day of the current month
            TimerangeProvider timerangeProvider = fetchTimereportContext.get().getTimerangeProvider();
            long amountOfDaysToDisplay = ChronoUnit.DAYS.between(timerangeProvider.getStartDate(), timerangeProvider.getEndDate());

            for (int days = 0; days <= amountOfDaysToDisplay; days++) {
                LocalDate currentColumnDate = timerangeProvider.getStartDate().plus(days, ChronoUnit.DAYS);
                String displayDate = FormattingUtil.formatDate(currentColumnDate);

                // worklog column
                taskTableView.getColumns().add(new WorklogTreeTableColumn(displayDate, currentColumnDate));
            }

            // also add another summary per task column
            taskTableView.getColumns().add(new TaskWorklogSummaryTreeTableColumn());

            lastUsedTimerangeProvider = Optional.of(fetchTimereportContext.get().getTimerangeProvider());
        }

        // only refresh items if changed from last time
        if (resultToDisplayChangedSinceLastRender) {
            LOGGER.debug("[{}] Refreshing items in TableView", getText());

            TreeItem<TaskWithWorklogs> root = taskTableView.getRoot();
            root.getChildren().clear();

            List<TaskWithWorklogs> displayResult = getDisplayResult(worklogResult);
            if (displayResult == null) {
                LOGGER.warn("[{}] returned a null resultset", getText());
                displayResult = new ArrayList<>();
            }

            List<TaskWithWorklogs> groupingResolvedDisplayResult = checkGroups(displayResult);

            Map<String, TreeItem<TaskWithWorklogs>> groupCriteriaToNode = new HashMap<>();
            for (TaskWithWorklogs taskWithWorklogs : groupingResolvedDisplayResult) {
                TreeItem<TaskWithWorklogs> addChildToThisTreeItem = root;

                if (taskWithWorklogs.getDistinctGroupCriteria().size() > 0) {
                    // checkGroups should have resolved this to only one distinct group
                    if (taskWithWorklogs.getDistinctGroupCriteria().size() > 1) {
                        throw new RuntimeException("This should not have happened");
                    }

                    String group = taskWithWorklogs.getDistinctGroupCriteria().get(0);
                    addChildToThisTreeItem = groupCriteriaToNode.get(group);
                    if (addChildToThisTreeItem == null) {
                        TaskWithWorklogs groupItem = new TaskWithWorklogs(false);
                        groupItem.setIsGroupRow(true);
                        groupItem.setIssue(group);

                        addChildToThisTreeItem = new TreeItem<>(groupItem);
                        addChildToThisTreeItem.setExpanded(true);

                        groupCriteriaToNode.put(group, addChildToThisTreeItem);
                        root.getChildren().add(addChildToThisTreeItem);
                    }
                }

                addChildToThisTreeItem.getChildren().add(new TreeItem<>(taskWithWorklogs));
            }

            // add another summary by group criteria
            if (groupCriteriaToNode.size() > 1) {
                groupCriteriaToNode.values().forEach(groupedTreeItem -> {
                    TaskWithWorklogs groupNode = groupedTreeItem.getValue();
                    groupedTreeItem.getChildren().forEach(childTaskWithWorklogsTreeItem -> childTaskWithWorklogsTreeItem.getValue().getWorklogItemList().forEach(worklogItem -> {
                        groupNode.addWorklogItem(worklogItem);
                    }));
                });
            }

            updateStatisticsData(displayResult);

            resultToDisplayChangedSinceLastRender = false;
        }
    }

    private List<TaskWithWorklogs> checkGroups(List<TaskWithWorklogs> displayResult) {
        List<TaskWithWorklogs> resolvedByGroups = new ArrayList<>();

        TaskWithWorklogs summaryRow = null;

        for (TaskWithWorklogs taskWithWorklogs : displayResult) {

            if (taskWithWorklogs.isSummaryRow()) {
                summaryRow = taskWithWorklogs;
                summaryRow.getDistinctGroupCriteria().clear();
                continue;
            }

            if (taskWithWorklogs.getDistinctGroupCriteria().size() > 0) {
                // group criteria present

                // create a copy for each group criteria
                Map<String, TaskWithWorklogs> groupToTaskWithworklogs = new HashMap<>(taskWithWorklogs.getDistinctGroupCriteria().size());
                taskWithWorklogs.getDistinctGroupCriteria().forEach(groupCriteria -> groupToTaskWithworklogs.put(groupCriteria, taskWithWorklogs.createCopy()));

                // remove worklogs not belonging to the group
                groupToTaskWithworklogs.entrySet().forEach(entry -> {
                    String requiredGroup = entry.getKey();
                    TaskWithWorklogs groupedTask = entry.getValue();
                    groupedTask.getDistinctGroupCriteria().removeIf(group -> !StringUtils.equals(group, requiredGroup));

                    for (Iterator<WorklogItem> iterator = groupedTask.getWorklogItemList().iterator(); iterator.hasNext(); ) {
                        WorklogItem item = iterator.next();
                        if (!StringUtils.equals(item.getGroup(), requiredGroup)) {
                            iterator.remove();
                        }
                    }

                    resolvedByGroups.add(groupedTask);
                });
            } else  {
                resolvedByGroups.add(taskWithWorklogs.createCopy());
            }
        }

        if (summaryRow != null) {
            resolvedByGroups.add(summaryRow);
        }

        return resolvedByGroups;
    }

    private void updateStatisticsData(List<TaskWithWorklogs> displayResult) {

        if (!SettingsUtil.loadSettings().getShowStatistics()) {
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

    protected Label getBoldLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, Font.getDefault().getSize()));
        return label;
    }

    public String getExcelDownloadSuggestedFilename() {
        if (!lastUsedTimerangeProvider.isPresent()) throw ExceptionUtil.getIllegalStateException("exceptions.excel.nodata");

        TimerangeProvider timerangeProvider = lastUsedTimerangeProvider.get();

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
        columnRendererList.add(new TaskDescriptionExcelColumn());

        TimerangeProvider timerangeProvider = fetchTimereportContext.get().getTimerangeProvider();
        LocalDate startDate = timerangeProvider.getStartDate();
        LocalDate endDate = timerangeProvider.getEndDate();

        long amountOfDaysToDisplay = ChronoUnit.DAYS.between(startDate, endDate);
        for (int days = 0; days <= amountOfDaysToDisplay; days++) {
            LocalDate currentColumnDate = timerangeProvider.getStartDate().plus(days, ChronoUnit.DAYS);
            DayOfWeek currentColumnDayOfWeek = currentColumnDate.getDayOfWeek();
            String displayDate = FormattingUtil.formatDate(currentColumnDate);
            columnRendererList.add(new WorklogExcelColumn(displayDate, currentColumnDate));
        }

        columnRendererList.add(new TaskWorklogSummaryExcelColumn());

        TreeItem<TaskWithWorklogs> root = taskTableView.getRoot();
        ObservableList<TreeItem<TaskWithWorklogs>> children = root.getChildren();

        for (int columnIndex = 0; columnIndex < columnRendererList.size(); columnIndex++) {
            ExcelColumnRenderer excelColumnRenderer = columnRendererList.get(columnIndex);
            excelColumnRenderer.renderCells(columnIndex, sheet, worklogResult.get(), children);
        }

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
