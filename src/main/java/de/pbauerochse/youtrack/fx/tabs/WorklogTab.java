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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Patrick Bauerochse
 * @since 02.07.15
 */
public abstract class WorklogTab extends Tab {

    protected static final String SUMMARY_COLUMN_OR_CELL_CSS_CLASS = "summary";
    protected static final String WEEKEND_COLUMN_OR_CELL_CSS_CLASS = "weekend";
    protected static final String ISSUE_CELL_CSS_CLASS = "issue-cell";

    protected List<TaskWithWorklogs> worklogsList;
    
    protected ReportTimerange timerange;

    protected ResourceBundle resourceBundle;

    protected SettingsUtil.Settings settings;

    public WorklogTab(String name, List<TaskWithWorklogs> worklogsList, ReportTimerange timerange, ResourceBundle resourceBundle, SettingsUtil.Settings settings) {
        super(name);
        this.worklogsList = worklogsList;
        this.timerange = timerange;
        this.resourceBundle = resourceBundle;
        this.settings = settings;

        setContent(getContentNode());
    }

    protected Node getContentNode() {

        Node taskView = getTaskView();
        Node statisticsView = getStatisticsView();

        if (taskView == null && statisticsView == null) {
            return new Label("Nothing implemented yet");
        } else if (statisticsView == null) {
            return taskView;
        }

        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.setDividerPosition(0, 0.8);
        splitPane.getItems().addAll(taskView, statisticsView);
        return splitPane;
    }

    protected Node getTaskView() {
        TableView<TaskWithWorklogs> tableView = new TableView<>();
        tableView.getColumns().add(getDescriptionColumn(tableView));
        addDetailColumns(tableView);

        tableView.getItems().addAll(worklogsList);

        AnchorPane anchorPane = new AnchorPane(tableView);
        anchorPane.setPadding(new Insets(6));

        AnchorPane.setTopAnchor(tableView, 0d);
        AnchorPane.setRightAnchor(tableView, 0d);
        AnchorPane.setBottomAnchor(tableView, 0d);
        AnchorPane.setLeftAnchor(tableView, 0d);

        return anchorPane;
    }

    protected Node getStatisticsView() {
        return null;
    }
    
    protected void addDetailColumns(TableView<TaskWithWorklogs> tableView) {
        // render tables for all days in the timerange
        TimerangeProvider timerangeProvider = timerange.getTimerangeProvider();
        long daysToDisplay = ChronoUnit.DAYS.between(timerangeProvider.getStartDate(), timerangeProvider.getEndDate());

        for (int days = 0; days <= daysToDisplay; days++) {
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
                            setText(StringUtils.EMPTY);
                            setTooltip(null);
                            getStyleClass().remove(SUMMARY_COLUMN_OR_CELL_CSS_CLASS);
                        } else {
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

            tableView.getColumns().add(column);
        }

        // also add another summary per task column
        TableColumn<TaskWithWorklogs, String> summaryPerTaskColumn = new TableColumn<>(resourceBundle.getString("view.main.summary"));
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
        tableView.getColumns().add(summaryPerTaskColumn);
    }

    protected TableColumn<TaskWithWorklogs, TaskWithWorklogs> getDescriptionColumn(TableView<TaskWithWorklogs> tableView) {
        TableColumn<TaskWithWorklogs, TaskWithWorklogs> descriptionColumn = new TableColumn<>(resourceBundle.getString("view.main.issue"));
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
                            setText(resourceBundle.getString("view.main.summary"));
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

                if (index < tableView.getItems().size()) {
                    TaskWithWorklogs clickedWorklogItem = tableView.getItems().get(index);
                    if (!clickedWorklogItem.isSummaryRow()) {
                        String issueUrl = String.format("%s/issue/%s", StringUtils.stripEnd(settings.getYoutrackUrl(), "/"), clickedWorklogItem.getIssue());
                        Platform.runLater(() -> WorklogViewer.getInstance().getHostServices().showDocument(issueUrl));
                    }
                }
            });

            return tableCell;
        });

        descriptionColumn.setPrefWidth(300);
        descriptionColumn.setMinWidth(300);

        return descriptionColumn;
    }

}
