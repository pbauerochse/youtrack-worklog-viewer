package de.pbauerochse.youtrack.fx;

import de.pbauerochse.youtrack.WorklogViewer;
import de.pbauerochse.youtrack.connector.YouTrackConnector;
import de.pbauerochse.youtrack.domain.ReportTimerange;
import de.pbauerochse.youtrack.domain.TimerangeProvider;
import de.pbauerochse.youtrack.domain.UserTaskWorklogs;
import de.pbauerochse.youtrack.domain.UserWorklogResult;
import de.pbauerochse.youtrack.util.FormattingUtil;
import de.pbauerochse.youtrack.util.SettingsUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

/**
 * @author Patrick Bauerochse
 * @since 01.04.15
 */
public class MainViewController implements Initializable {

    private static final String SUMMARY_COLUMN_OR_CELL_CSS_CLASS = "summary";
    private static final String WEEKEND_COLUMN_OR_CELL_CSS_CLASS = "weekend";
    private static final String ISSUE_CELL_CSS_CLASS = "issue-cell";

    @FXML
    private ComboBox<ReportTimerange> timerangeComboBox;

    @FXML
    private Button fetchWorklogButton;

    @FXML
    private Button settingsButton;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Text progressText;

    @FXML
    private TableView<UserTaskWorklogs> worklogTableView;

    private ResourceBundle resources;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.resources = resources;

        // settings button click
        settingsButton.setOnAction(event -> {
            Platform.runLater(() -> {
                try {
                    Parent settingsContent = FXMLLoader.load(MainViewController.class.getResource("/fx/views/settings.fxml"), resources);

                    Scene settingsScene = new Scene(settingsContent);
                    Stage settingsStage = new Stage();
                    settingsStage.initOwner(((Node) event.getSource()).getScene().getWindow());
                    settingsStage.initStyle(StageStyle.UTILITY);
                    settingsStage.initModality(Modality.APPLICATION_MODAL);
                    settingsStage.setResizable(false);
                    settingsStage.setTitle(resources.getString("view.settings.title"));
                    settingsStage.setScene(settingsScene);
                    settingsStage.showAndWait();
                } catch (IOException e) {
                    throw new RuntimeException(resources.getString("exceptions.settingsview.io"), e);
                }
            });
        });


        // prepopulate timerange dropdown
        timerangeComboBox.setConverter(new StringConverter<ReportTimerange>() {
            @Override
            public String toString(ReportTimerange object) {
                return resources.getString(object.getLabelKey());
            }

            @Override
            public ReportTimerange fromString(String string) {
                for (ReportTimerange timerange : ReportTimerange.values()) {
                    if (StringUtils.equals(resources.getString(timerange.getLabelKey()), string)) {
                        return timerange;
                    }
                }
                return null;
            }
        });
        timerangeComboBox.getItems().addAll(ReportTimerange.values());
        timerangeComboBox.getSelectionModel().select(ReportTimerange.THIS_WEEK);    // preselect "this week"

        SettingsUtil.Settings settings = SettingsUtil.loadSettings();

        // fetch worklog button click
        fetchWorklogButton.setOnAction(clickEvent -> {
            if (StringUtils.isBlank(settings.getYoutrackUrl()) || StringUtils.isBlank(settings.getYoutrackUsername()) || StringUtils.isBlank(settings.getYoutrackPassword())) {
                // connection data missing hence trigger settings button click and show warning
                progressText.setText(resources.getString("view.main.warning.settingsblank"));
                settingsButton.fire();
            } else {
                // settings present, fetch worklogs
                fetchWorklogs(settings, timerangeComboBox.getSelectionModel().getSelectedItem());
            }
        });
    }

    private void fetchWorklogs(SettingsUtil.Settings settings, ReportTimerange timerange) {
        Task<UserWorklogResult> worklogTaskForUser = YouTrackConnector.getInstance().getWorklogTaskForUser(settings.getYoutrackUrl(), settings.getYoutrackUsername(), settings.getYoutrackPassword(), timerange);

        // disabled worklog button while still waiting for response
        worklogTaskForUser.stateProperty().addListener((observable, oldValue, newValue) -> {
            fetchWorklogButton.setDisable(newValue == Worker.State.RUNNING || newValue == Worker.State.SCHEDULED);
        });

        // success handler
        worklogTaskForUser.setOnSucceeded(event -> {
            UserWorklogResult result = (UserWorklogResult) event.getSource().getValue();
            displayResult(result, timerange, settings);
        });

        // error handler
        worklogTaskForUser.setOnFailed(event -> {
            Throwable throwable = event.getSource().getException();

            progressBar.progressProperty().unbind();
            progressText.textProperty().unbind();
            progressBar.progressProperty().set(0);

            if (throwable != null) {
                progressText.setText(throwable.getMessage());
                throwable.printStackTrace();
            } else {
                progressText.setText(resources.getString("exceptions.main.worker.unknown"));
            }
        });

        // bind progressbar and -text property to task
        progressText.textProperty().unbind();
        progressBar.progressProperty().unbind();
        progressText.textProperty().bind(worklogTaskForUser.messageProperty());
        progressBar.progressProperty().bind(worklogTaskForUser.progressProperty());

        // start task
        Thread thread = new Thread(worklogTaskForUser);
        thread.setDaemon(true);
        thread.start();
    }

    private void displayResult(UserWorklogResult result, ReportTimerange timerange, SettingsUtil.Settings settings) {
        // clear tableview columns since they
        // might have changed, depending on the set timerange
        worklogTableView.getColumns().clear();
        worklogTableView.getColumns().add(getDescriptionColumn(settings));

        // render tables for all days in the timerange
        TimerangeProvider timerangeProvider = timerange.getTimerangeProvider();
        long daysToDisplay = ChronoUnit.DAYS.between(timerangeProvider.getStartDate(), timerangeProvider.getEndDate());

        for (int days = 0; days <= daysToDisplay; days++) {
            LocalDate currentColumnDate = timerangeProvider.getStartDate().plus(days, ChronoUnit.DAYS);
            DayOfWeek currentColumnDayOfWeek = currentColumnDate.getDayOfWeek();
            String displayDate = FormattingUtil.formatDate(currentColumnDate);

            // create column
            TableColumn<UserTaskWorklogs, UserTaskWorklogs> column = new TableColumn<>(displayDate);
            column.setSortable(false);
            column.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue()));
            column.setCellFactory(param -> {
                TableCell<UserTaskWorklogs, UserTaskWorklogs> cell = new TableCell<UserTaskWorklogs, UserTaskWorklogs>() {
                    @Override
                    protected void updateItem(UserTaskWorklogs item, boolean empty) {
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

            worklogTableView.getColumns().addAll(column);
        }

        // also add another summary per task column
        TableColumn<UserTaskWorklogs, String> summaryPerTaskColumn = new TableColumn<>(resources.getString("view.main.summary"));
        summaryPerTaskColumn.setSortable(false);
        summaryPerTaskColumn.setCellValueFactory(param -> new SimpleStringProperty(FormattingUtil.formatMinutes(param.getValue().getTotalInMinutes())));
        summaryPerTaskColumn.setCellFactory(param -> {
            TableCell<UserTaskWorklogs, String> summaryCell = new TableCell<UserTaskWorklogs, String>() {
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
        worklogTableView.getColumns().addAll(summaryPerTaskColumn);

        // update items
        worklogTableView.getItems().clear();
        worklogTableView.getItems().addAll(result.getSummariesAsList());
    }

    private TableColumn<UserTaskWorklogs, UserTaskWorklogs> descriptionColumn;

    private TableColumn<UserTaskWorklogs, UserTaskWorklogs> getDescriptionColumn(SettingsUtil.Settings settings) {
        if (descriptionColumn == null) {
            descriptionColumn = new TableColumn<>(resources.getString("view.main.issue"));
            descriptionColumn.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue()));
            descriptionColumn.setCellFactory(param -> {
                TableCell<UserTaskWorklogs, UserTaskWorklogs> tableCell = new TableCell<UserTaskWorklogs, UserTaskWorklogs>() {

                    @Override
                    protected void updateItem(UserTaskWorklogs item, boolean empty) {
                        if (empty) {
                            setText(StringUtils.EMPTY);
                            setTooltip(null);
                        } else {

                            if (item.isSummaryRow()) {
                                setText(resources.getString("view.main.summary"));
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
                    TableCell<UserTaskWorklogs, UserTaskWorklogs> cell = (TableCell) event.getSource();
                    int index = cell.getIndex();

                    if (index < worklogTableView.getItems().size()) {
                        UserTaskWorklogs clickedWorklogItem = worklogTableView.getItems().get(index);
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
        }

        return descriptionColumn;
    }
}
