package de.pbauerochse.worklogviewer.fx.tabs;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.pbauerochse.worklogviewer.domain.TimerangeProvider;
import de.pbauerochse.worklogviewer.excel.ExcelColumnRenderer;
import de.pbauerochse.worklogviewer.excel.columns.TaskDescriptionExcelColumn;
import de.pbauerochse.worklogviewer.excel.columns.TaskStatusExcelColumn;
import de.pbauerochse.worklogviewer.excel.columns.TaskWorklogSummaryExcelColumn;
import de.pbauerochse.worklogviewer.excel.columns.WorklogExcelColumn;
import de.pbauerochse.worklogviewer.fx.tablecolumns.TaskDescriptionTreeTableColumn;
import de.pbauerochse.worklogviewer.fx.tablecolumns.TaskStatusTreeTableColumn;
import de.pbauerochse.worklogviewer.fx.tablecolumns.TaskWorklogSummaryTreeTableColumn;
import de.pbauerochse.worklogviewer.fx.tablecolumns.WorklogTreeTableColumn;
import de.pbauerochse.worklogviewer.fx.tabs.domain.DisplayData;
import de.pbauerochse.worklogviewer.fx.tabs.domain.DisplayDayEntry;
import de.pbauerochse.worklogviewer.fx.tabs.domain.DisplayRow;
import de.pbauerochse.worklogviewer.fx.tasks.FetchTimereportContext;
import de.pbauerochse.worklogviewer.util.ExceptionUtil;
import de.pbauerochse.worklogviewer.util.FormattingUtil;
import de.pbauerochse.worklogviewer.util.SettingsUtil;
import de.pbauerochse.worklogviewer.youtrack.domain.TaskWithWorklogs;
import de.pbauerochse.worklogviewer.youtrack.domain.WorklogItem;
import de.pbauerochse.worklogviewer.youtrack.domain.WorklogReport;
import javafx.collections.ObservableList;
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
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author Patrick Bauerochse
 * @since 02.07.15
 */
public abstract class WorklogTab extends Tab {

    protected static final Collator COLLATOR = Collator.getInstance(Locale.GERMANY);

    // height adjustment parameters for bargraph
    private static final int HEIGHT_PER_Y_AXIS_ELEMENT = 40;
    private static final int HEIGHT_PER_X_AXIS_ELEMENT = 35;
    private static final int ADDITIONAL_HEIGHT = 150;

    private Logger LOGGER = LoggerFactory.getLogger(WorklogTab.class);

    private TreeTableView<DisplayRow> taskTableView;

    private Optional<TimerangeProvider> lastUsedTimerangeProvider = Optional.empty();

    private Optional<Integer> lastHighlightState = Optional.empty();

    private Optional<Integer> lastCollapseState = Optional.empty();

    private Optional<FetchTimereportContext> fetchTimereportContext = Optional.empty();

    private boolean resultToDisplayChangedSinceLastRender;

    private VBox statisticsView;

    private TaskDescriptionTreeTableColumn taskDescriptionTreeTableColumn;

    private TaskStatusTreeTableColumn taskStatusTreeTableColumn;

    private Optional<DisplayData> resultItemsToDisplay = Optional.empty();

    protected abstract List<TaskWithWorklogs> getFilteredList(List<TaskWithWorklogs> tasks);

    public WorklogTab(String name) {
        super(name);

        setContent(getContentNode());

        // when this tab becomes active render the data
        selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && resultToDisplayChangedSinceLastRender) {
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
     * @param timereportContext The TaskWithWorklogs to show in this tab
     */
    public void updateItems(FetchTimereportContext timereportContext) {
        this.fetchTimereportContext = Optional.of(timereportContext);

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
            taskTableView = new TreeTableView<>(new TreeItem<>());
            taskTableView.setShowRoot(false);
        }

        if (taskDescriptionTreeTableColumn == null) {
            taskDescriptionTreeTableColumn = new TaskDescriptionTreeTableColumn();
            taskDescriptionTreeTableColumn.setPrefWidth(300);
            taskDescriptionTreeTableColumn.setMinWidth(300);
        }

        if (taskStatusTreeTableColumn == null) {
            taskStatusTreeTableColumn = new TaskStatusTreeTableColumn();
            taskStatusTreeTableColumn.setPrefWidth(30);
            taskStatusTreeTableColumn.setMinWidth(30);
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
        Optional<FetchTimereportContext> reportContextOptional = this.fetchTimereportContext;
        if (!reportContextOptional.isPresent() || !reportContextOptional.get().getResult().isPresent() || !resultToDisplayChangedSinceLastRender) {
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

        FetchTimereportContext timereportContext = reportContextOptional.get();
        TimerangeProvider timerangeProvider = timereportContext.getTimerangeProvider();

        // render the table columns if the timerange changed from last result
        if (!lastUsedTimerangeProvider.isPresent() || !lastUsedTimerangeProvider.get().equals(timerangeProvider) || (lastCollapseState.isPresent() && lastCollapseState.get() != settings.getCollapseState()) || (lastHighlightState.isPresent() && lastHighlightState.get() != settings.getHighlightState())) {

            LOGGER.debug("[{}] Regenerating columns for timerange {}", getText(), timerangeProvider.getReportTimerange().name());
            taskTableView.getColumns().clear();
            taskTableView.getColumns().add(taskStatusTreeTableColumn);
            taskTableView.getColumns().add(taskDescriptionTreeTableColumn);

            // render tables for all days in the selected timerange
            // e.g. timerange current month renders a column for
            // each day of the current month
            long amountOfDaysToDisplay = ChronoUnit.DAYS.between(timerangeProvider.getStartDate(), timerangeProvider.getEndDate());

            for (int days = 0; days <= amountOfDaysToDisplay; days++) {
                LocalDate currentColumnDate = timerangeProvider.getStartDate().plus(days, ChronoUnit.DAYS);
                String displayDate = FormattingUtil.formatDate(currentColumnDate);

                // worklog column
                taskTableView.getColumns().add(new WorklogTreeTableColumn(displayDate, currentColumnDate));
            }

            // also add another summary per task column
            taskTableView.getColumns().add(new TaskWorklogSummaryTreeTableColumn());

            lastUsedTimerangeProvider = Optional.of(timerangeProvider);
            lastCollapseState = Optional.of(settings.getCollapseState());
            lastHighlightState = Optional.of(settings.getHighlightState());
        }

        // refresh data
        LOGGER.debug("[{}] Refreshing items in TableView", getText());

        TreeItem<DisplayRow> root = taskTableView.getRoot();
        root.getChildren().clear();

        DisplayData displayData = getDisplayData(timereportContext, resultToDisplayChangedSinceLastRender);
        root.getChildren().addAll(displayData.getTreeRows());

        resultToDisplayChangedSinceLastRender = false;
    }

    private DisplayData getDisplayData(FetchTimereportContext reportContext, boolean changedSinceLastRender) {

        if (changedSinceLastRender) {
            LOGGER.debug("Refreshing display data");
            DisplayData displayData = new DisplayData();

            WorklogReport result = reportContext.getResult().get();
            ImmutableList<TaskWithWorklogs> originalTasks = result.getTasks();

            // create a copy of the original task list
            // and pass it on to the getFilteredList method
            // which then may freely modify the list and its items
            List<TaskWithWorklogs> deepCopiedList = Lists.newArrayList();
            originalTasks.forEach(taskWithWorklogs -> deepCopiedList.add(taskWithWorklogs.createDeepCopy()));
            List<TaskWithWorklogs> filteredList = getFilteredList(deepCopiedList);

            // render the treetabledata
            if (reportContext.getGroupByCategory().isPresent()) {
                // grouping present
                processWithGrouping(filteredList, displayData);
            } else {
                // no grouping
                processWithoutGrouping(filteredList, displayData);
            }

            // add grandtotal column
            DisplayRow grandTotal = new DisplayRow();
            grandTotal.setIsGrandTotalSummary(true);
            displayData.addRow(new TreeItem<>(grandTotal));

            filteredList.stream()
                    .map(TaskWithWorklogs::getWorklogItemList)
                    .flatMap(Collection::stream)
                    .forEach(worklogItem -> {
                        LocalDate date = worklogItem.getDate();

                        DisplayDayEntry workdayEntry = grandTotal.getWorkdayEntry(date)
                                .orElseGet(() -> {
                                    DisplayDayEntry displayDayEntry = new DisplayDayEntry();
                                    displayDayEntry.setDate(date);

                                    grandTotal.addDisplayDayEntry(displayDayEntry);

                                    return displayDayEntry;
                                });

                        workdayEntry.getSpentTime().addAndGet(worklogItem.getDurationInMinutes());
                    });

            // call statistics update
            updateStatisticsData(filteredList);

            resultItemsToDisplay = Optional.of(displayData);
        }

        return resultItemsToDisplay.get();
    }

    private void processWithGrouping(List<TaskWithWorklogs> tasks, DisplayData displayData) {
        LOGGER.debug("Processing with grouping");
        List<String> distinctGroupByCriteria = tasks.stream()
                .map(TaskWithWorklogs::getDistinctGroupByCriteriaValues)
                .flatMap(Collection::stream)
                .distinct()
                .sorted(COLLATOR)
                .collect(Collectors.toList());

        distinctGroupByCriteria.forEach(groupByCriteria -> {
            LOGGER.debug("Gathering data for group criteria value {}", groupByCriteria);
            DisplayRow groupCaptionRow = new DisplayRow();
            groupCaptionRow.setIsGroupContainer(true);
            groupCaptionRow.setLabel(groupByCriteria);

            TreeItem<DisplayRow> groupRow = new TreeItem<>(groupCaptionRow);
            groupRow.setExpanded(true);
            Map<String, DisplayRow> ticketIdToDisplayRow = Maps.newHashMap();

            // add sub rows to groupRow
            tasks.stream()
                    .filter(taskWithWorklogs -> taskWithWorklogs.getDistinctGroupByCriteriaValues().contains(groupByCriteria))
                    .sorted((o1, o2) -> COLLATOR.compare(o1.getIssue(), o2.getIssue()))
                    .forEach(taskWithWorklogs -> {
                        // this task with worklogs contains at least one workitem
                        // having the group by criteria

                        DisplayRow ticketRowWithinThisGroup = ticketIdToDisplayRow.get(taskWithWorklogs.getIssue());
                        if (ticketRowWithinThisGroup == null) {
                            ticketRowWithinThisGroup = new DisplayRow();
                            ticketRowWithinThisGroup.setLabel(taskWithWorklogs.getSummary());
                            ticketRowWithinThisGroup.setIssueId(taskWithWorklogs.getIssue());
                            ticketRowWithinThisGroup.setResolvedDate(taskWithWorklogs.getResolved());
                            groupRow.getChildren().add(new TreeItem<>(ticketRowWithinThisGroup));
                            ticketIdToDisplayRow.put(taskWithWorklogs.getIssue(), ticketRowWithinThisGroup);
                        }

                        DisplayRow ticketRowWithinThisGroupAsFinal = ticketRowWithinThisGroup;

                        taskWithWorklogs.getWorklogItemList().stream()
                                .filter(worklogItem -> StringUtils.equals(worklogItem.getGroup(), groupByCriteria))
                                .sorted((o1, o2) -> o1.getDate().compareTo(o2.getDate()))
                                .forEach(worklogItem -> {
                                    // this worklog item matches the critera
                                    // add workday entry to current row
                                    LocalDate date = worklogItem.getDate();

                                    DisplayDayEntry workdayEntry = ticketRowWithinThisGroupAsFinal.getWorkdayEntry(date)
                                            .orElseGet(() -> {
                                                DisplayDayEntry displayDayEntry = new DisplayDayEntry();
                                                displayDayEntry.setDate(date);
                                                ticketRowWithinThisGroupAsFinal.addDisplayDayEntry(displayDayEntry);

                                                return displayDayEntry;
                                            });

                                    workdayEntry.getSpentTime().addAndGet(worklogItem.getDurationInMinutes());

                                    // also add up the spent time in the group header per group
                                    workdayEntry = groupCaptionRow.getWorkdayEntry(date)
                                            .orElseGet(() -> {
                                                DisplayDayEntry newWorkdayEntry = new DisplayDayEntry();
                                                newWorkdayEntry.setDate(date);
                                                groupCaptionRow.addDisplayDayEntry(newWorkdayEntry);
                                                return newWorkdayEntry;
                                            });
                                    workdayEntry.getSpentTime().addAndGet(worklogItem.getDurationInMinutes());
                                });
                    });

            // add groupRow to result
            displayData.addRow(groupRow);
        });
    }

    private void processWithoutGrouping(List<TaskWithWorklogs> tasks, DisplayData displayData) {
        LOGGER.debug("Processing without grouping");
        tasks.stream()
                .sorted((o1, o2) -> COLLATOR.compare(o1.getIssue(), o2.getIssue()))
                .forEach(taskWithWorklogs -> {
                    DisplayRow row = new DisplayRow();
                    row.setIssueId(taskWithWorklogs.getIssue());
                    row.setLabel(taskWithWorklogs.getSummary());
                    row.setResolvedDate(taskWithWorklogs.getResolved());

                    taskWithWorklogs.getWorklogItemList().forEach(worklogItem -> {
                        LocalDate date = worklogItem.getDate();
                        DisplayDayEntry workdayEntry = row.getWorkdayEntry(date)
                                .orElseGet(() -> {
                                    DisplayDayEntry newWorkdayEntry = new DisplayDayEntry();
                                    newWorkdayEntry.setDate(date);
                                    row.addDisplayDayEntry(newWorkdayEntry);
                                    return newWorkdayEntry;
                                });

                        workdayEntry.getSpentTime().addAndGet(worklogItem.getDurationInMinutes());
                    });

                    displayData.addRow(new TreeItem<>(row));
                });
    }

    private void updateStatisticsData(List<TaskWithWorklogs> displayResult) {

        if (!SettingsUtil.loadSettings().isShowStatistics()) {
            return;
        }

        statisticsView.getChildren().clear();

        WorklogStatistics statistics = new WorklogStatistics();

        // generic statistics
        displayResult.forEach(taskWithWorklogs -> {
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

        });

        // render grid and bar graph
        final AtomicInteger currentGridRow = new AtomicInteger(0);

        GridPane employeeProjectSummaryGrid = new GridPane();
        employeeProjectSummaryGrid.setHgap(5);
        employeeProjectSummaryGrid.setVgap(5);

        NumberAxis projectEmployeeXAxis = new NumberAxis();
        projectEmployeeXAxis.setLabel(FormattingUtil.getFormatted("view.statistics.timespentinhours"));
        projectEmployeeXAxis.setTickLabelRotation(90);

        NumberAxis employeeProjectXAxis = new NumberAxis();
        employeeProjectXAxis.setLabel(FormattingUtil.getFormatted("view.statistics.timespentinhours"));
        employeeProjectXAxis.setTickLabelRotation(90);

        CategoryAxis projectEmployeeYAxis = new CategoryAxis();
        CategoryAxis employeeProjectYAxis = new CategoryAxis();

        StackedBarChart<Number, String> projectEmployeeBargraph = new StackedBarChart<>(projectEmployeeXAxis, projectEmployeeYAxis);
        StackedBarChart<Number, String> employeeProjectBargraph = new StackedBarChart<>(employeeProjectXAxis, employeeProjectYAxis);

        projectEmployeeBargraph.setTitle(FormattingUtil.getFormatted("view.statistics.byprojectandemployee"));
        employeeProjectBargraph.setTitle(FormattingUtil.getFormatted("view.statistics.byemployeeandproject"));

        Set<String> projectsToDisplay = new HashSet<>();
        displayResult.forEach(taskWithWorklogs -> {
            projectsToDisplay.add(taskWithWorklogs.getProject());
        });
        int projectEmployeeBargraphPreferedHeight = HEIGHT_PER_Y_AXIS_ELEMENT * projectsToDisplay.size() + HEIGHT_PER_X_AXIS_ELEMENT * statistics.getEmployeeToTotaltimeSpent().keySet().size() + ADDITIONAL_HEIGHT;
        projectEmployeeBargraph.setPrefHeight(projectEmployeeBargraphPreferedHeight);
        VBox.setVgrow(projectEmployeeBargraph, Priority.ALWAYS);

        int employeeProjectBargraphPreferedHeight = HEIGHT_PER_Y_AXIS_ELEMENT * statistics.getEmployeeToProjectToWorktime().keySet().size() + HEIGHT_PER_X_AXIS_ELEMENT * projectsToDisplay.size() + ADDITIONAL_HEIGHT;
        employeeProjectBargraph.setPrefHeight(employeeProjectBargraphPreferedHeight);
        VBox.setVgrow(employeeProjectBargraph, Priority.ALWAYS);

        Map<String, XYChart.Series<Number, String>> projectNameToSeries = Maps.newHashMap();

        statistics.getEmployeeToProjectToWorktime().keySet().stream()
                .sorted(COLLATOR::compare)
                .forEach(employee -> {

                    // employee headline label
                    Set<String> totalDistinctTasksOfEmployee = statistics.getEmployeeToTotalDistinctTasks().get(employee);
                    Label employeeLabel = getBoldLabel(FormattingUtil.getFormatted("view.statistics.somethingtoamountoftickets", employee, totalDistinctTasksOfEmployee.size()));
                    employeeLabel.setPadding(new Insets(20, 0, 0, 0));
                    GridPane.setConstraints(employeeLabel, 0, currentGridRow.getAndIncrement());
                    GridPane.setColumnSpan(employeeLabel, 4);
                    employeeProjectSummaryGrid.getChildren().addAll(employeeLabel);

                    // bar graph data container
                    XYChart.Series<Number, String> projectEmployeeSeries = new XYChart.Series<>();
                    projectEmployeeSeries.setName(employee);
                    projectEmployeeBargraph.getData().add(projectEmployeeSeries);

                    // time spent per project
                    Map<String, AtomicLong> projectToWorktime = statistics.getEmployeeToProjectToWorktime().get(employee);
                    Map<String, Label> projectToPercentageLabel = Maps.newHashMap();

                    projectToWorktime.keySet().stream()
                            .sorted(COLLATOR::compare)
                            .forEach(projectName -> {

                                XYChart.Series<Number, String> employeeProjectSeries = projectNameToSeries.get(projectName);
                                if (employeeProjectSeries == null) {
                                    employeeProjectSeries = new XYChart.Series<>();
                                    employeeProjectSeries.setName(projectName);
                                    employeeProjectBargraph.getData().add(employeeProjectSeries);
                                    projectNameToSeries.put(projectName, employeeProjectSeries);
                                }

                                // percentage label
                                Label percentageLabel = getBoldLabel("PLACEHOLDER");
                                percentageLabel.setAlignment(Pos.CENTER_RIGHT);
                                percentageLabel.setPadding(new Insets(0, 0, 0, 20));
                                GridPane.setConstraints(percentageLabel, 1, currentGridRow.get());
                                GridPane.setHalignment(percentageLabel, HPos.RIGHT);
                                projectToPercentageLabel.put(projectName, percentageLabel);

                                // project label
                                Set<String> distinctTasksPerProject = statistics.getEmployeeToProjectToDistinctTasks().get(employee).get(projectName);
                                Label projectLabel = getBoldLabel(FormattingUtil.getFormatted("view.statistics.somethingtoamountoftickets", projectName, distinctTasksPerProject.size()));
                                GridPane.setConstraints(projectLabel, 2, currentGridRow.get());

                                // time spent for project label
                                long timespentInMinutes = projectToWorktime.get(projectName).longValue();
                                Label timespentLabel = new Label(FormattingUtil.formatMinutes(timespentInMinutes, true));
                                GridPane.setConstraints(timespentLabel, 3, currentGridRow.get());
                                GridPane.setHgrow(timespentLabel, Priority.ALWAYS);
                                GridPane.setHalignment(timespentLabel, HPos.RIGHT);

                                employeeProjectSummaryGrid.getChildren().addAll(percentageLabel, projectLabel, timespentLabel);
                                currentGridRow.incrementAndGet();

                                // bargraph data
                                projectEmployeeSeries.getData().add(new XYChart.Data<>(timespentInMinutes / 60d, projectName));
                                employeeProjectSeries.getData().addAll(new XYChart.Data<>(timespentInMinutes / 60d, employee));
                            });

                    // total time spent
                    Label totalLabel = getBoldLabel(FormattingUtil.getFormatted("view.statistics.totaltimespent"));
                    GridPane.setConstraints(totalLabel, 0, currentGridRow.get());
                    GridPane.setColumnSpan(totalLabel, 4);

                    Label timespentLabel = new Label(FormattingUtil.formatMinutes(statistics.getEmployeeToTotaltimeSpent().get(employee).get(), true));
                    GridPane.setConstraints(timespentLabel, 3, currentGridRow.get());
                    GridPane.setHgrow(timespentLabel, Priority.ALWAYS);
                    GridPane.setHalignment(timespentLabel, HPos.RIGHT);
                    employeeProjectSummaryGrid.getChildren().addAll(totalLabel, timespentLabel);

                    // set label now that we can calculate the percentage
                    projectToWorktime.keySet().forEach(projectName -> {
                        Label percentageLabel = projectToPercentageLabel.get(projectName);

                        double totalSpentTime = statistics.getEmployeeToTotaltimeSpent().get(employee).doubleValue();
                        double spentTimeOnProject = projectToWorktime.get(projectName).doubleValue();

                        double percentage = spentTimeOnProject / totalSpentTime;
                        String percentageFormatted = FormattingUtil.formatPercentage(percentage);
                        percentageLabel.setText(percentageFormatted);
                    });

                    currentGridRow.incrementAndGet();
                });

        // employeeProjectBargraph

        statisticsView.getChildren().addAll(employeeProjectSummaryGrid, projectEmployeeBargraph, employeeProjectBargraph);

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
        columnRendererList.add(new TaskStatusExcelColumn());
        columnRendererList.add(new TaskDescriptionExcelColumn());

        TimerangeProvider timerangeProvider = fetchTimereportContext.get().getTimerangeProvider();
        LocalDate startDate = timerangeProvider.getStartDate();
        LocalDate endDate = timerangeProvider.getEndDate();

        long amountOfDaysToDisplay = ChronoUnit.DAYS.between(startDate, endDate);
        for (int days = 0; days <= amountOfDaysToDisplay; days++) {
            LocalDate currentColumnDate = timerangeProvider.getStartDate().plus(days, ChronoUnit.DAYS);
            String displayDate = FormattingUtil.formatDate(currentColumnDate);
            columnRendererList.add(new WorklogExcelColumn(displayDate, currentColumnDate));
        }

        columnRendererList.add(new TaskWorklogSummaryExcelColumn());

        TreeItem<DisplayRow> root = taskTableView.getRoot();
        ObservableList<TreeItem<DisplayRow>> children = root.getChildren();

        for (int columnIndex = 0; columnIndex < columnRendererList.size(); columnIndex++) {
            ExcelColumnRenderer excelColumnRenderer = columnRendererList.get(columnIndex);
            excelColumnRenderer.renderCells(columnIndex, sheet, children, fetchTimereportContext.get().getGroupByCategory().isPresent());
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
