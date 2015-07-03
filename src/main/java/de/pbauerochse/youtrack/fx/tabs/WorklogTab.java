package de.pbauerochse.youtrack.fx.tabs;

import de.pbauerochse.youtrack.WorklogViewer;
import de.pbauerochse.youtrack.domain.ReportTimerange;
import de.pbauerochse.youtrack.domain.TaskWithWorklogs;
import de.pbauerochse.youtrack.domain.TimerangeProvider;
import de.pbauerochse.youtrack.util.FormattingUtil;
import de.pbauerochse.youtrack.util.SettingsUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * @author Patrick Bauerochse
 * @since 02.07.15
 */
public abstract class WorklogTab extends Tab {

    protected static final String SUMMARY_COLUMN_OR_CELL_CSS_CLASS = "summary";
    protected static final String WEEKEND_COLUMN_OR_CELL_CSS_CLASS = "weekend";
    protected static final String ISSUE_CELL_CSS_CLASS = "issue-cell";

    private Logger LOGGER = LoggerFactory.getLogger(WorklogTab.class);

    protected TableView<TaskWithWorklogs> taskTableView;

    protected Optional<ReportTimerange> lastUsedTimerange = Optional.empty();

    protected Optional<List<TaskWithWorklogs>> resultToDisplay = Optional.empty();
    protected Optional<ReportTimerange> timerangeToDisplay = Optional.empty();

    protected boolean resultToDisplayChangedSinceLastRender;

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
     * @param timerange The selected ReportingTimerange
     */
    public void updateItems(List<TaskWithWorklogs> worklogList, ReportTimerange timerange) {
        resultToDisplay = Optional.of(worklogList);
        timerangeToDisplay = Optional.of(timerange);
        resultToDisplayChangedSinceLastRender = true;

        if (isSelected()) {
            refreshWorklogTableViewAndResults();
        }
    }

    protected Node getContentNode() {

        Node taskView = getTaskView();
        Node statisticsView = getStatisticsView();

        if (taskView == null && statisticsView == null) {
            // both views were null so this tab is not implemented yet
            // should actually not happen
            return new Label("Nothing implemented yet");
        } else if (statisticsView == null) {
            // only task view is present so only use that one
            return taskView;
        }

        // both statistics and task view present
        // show both in a split pane
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.setDividerPosition(0, 0.8);
        splitPane.getItems().addAll(taskView, statisticsView);
        return splitPane;
    }

    protected Node getTaskView() {
        taskTableView = new TableView<>();

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
        if (!timerangeToDisplay.isPresent() || !resultToDisplay.isPresent() || !resultToDisplayChangedSinceLastRender) {
            LOGGER.debug("[{}] No results to display or data not changed. Not refreshing TableView and data", getText());
            return;
        }

        // render the table columns if the timerange changed from last result
        if (!lastUsedTimerange.isPresent() || lastUsedTimerange.get() != timerangeToDisplay.get()) {

            LOGGER.debug("[{}] Regenerating columns for timerange {}", getText(), timerangeToDisplay.get().name());
            taskTableView.getColumns().clear();
            taskTableView.getColumns().add(getDescriptionColumn());

            // render tables for all days in the selected timerange
            // e.g. timerange current month renders a column for
            // each day of the current month
            TimerangeProvider timerangeProvider = timerangeToDisplay.get().getTimerangeProvider();
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

            lastUsedTimerange = Optional.of(timerangeToDisplay.get());
        }

        // only refresh items if changed from last time
        if (resultToDisplayChangedSinceLastRender) {
            LOGGER.debug("[{}] Refreshing items in TableView", getText());
            taskTableView.getItems().clear();
            taskTableView.getItems().addAll(resultToDisplay.get());

            updateStatisticsData();

            resultToDisplayChangedSinceLastRender = false;
        }
    }

    protected Node getStatisticsView() {
        return null;
    }

    protected void updateStatisticsData() {

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
                            String issueUrl = String.format("%s/issue/%s", StringUtils.stripEnd(settings.getYoutrackUrl(), "/"), clickedWorklogItem.getIssue());
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

}
