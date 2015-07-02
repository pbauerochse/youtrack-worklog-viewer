package de.pbauerochse.youtrack.fx;

import de.pbauerochse.youtrack.WorklogViewer;
import de.pbauerochse.youtrack.connector.YouTrackConnector;
import de.pbauerochse.youtrack.domain.ReportTimerange;
import de.pbauerochse.youtrack.domain.TimerangeProvider;
import de.pbauerochse.youtrack.domain.UserTaskWorklogs;
import de.pbauerochse.youtrack.domain.WorklogResult;
import de.pbauerochse.youtrack.util.ExceptionUtil;
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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

/**
 * @author Patrick Bauerochse
 * @since 01.04.15
 */
public class MainViewController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainViewController.class);

    private static final String SUMMARY_COLUMN_OR_CELL_CSS_CLASS = "summary";
    private static final String WEEKEND_COLUMN_OR_CELL_CSS_CLASS = "weekend";
    private static final String ISSUE_CELL_CSS_CLASS = "issue-cell";

    @FXML
    private ComboBox<ReportTimerange> timerangeComboBox;

    @FXML
    private Button fetchWorklogButton;

    @FXML
    private MenuItem settingsMenuItem;

    @FXML
    private MenuItem logMessagesMenuItem;

    @FXML
    private MenuItem exitMenuItem;

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
        LOGGER.debug("Initializing main view");

        // prepopulate timerange dropdown
        timerangeComboBox.setConverter(getTimerangeComboBoxConverter(resources));
        timerangeComboBox.getItems().addAll(ReportTimerange.values());
        timerangeComboBox.getSelectionModel().select(ReportTimerange.THIS_WEEK);    // preselect "this week"

        // menu items click
        settingsMenuItem.setOnAction(event -> showSettingsDialogue());
        exitMenuItem.setOnAction(event -> WorklogViewer.getInstance().requestShutdown());
        logMessagesMenuItem.setOnAction(event -> showLogMessagesDialogue());

        // fetch worklog button click
        SettingsUtil.Settings settings = SettingsUtil.loadSettings();
        fetchWorklogButton.setOnAction(clickEvent -> handleFetchWorklogButtonClick(settings));
    }

    /**
     * Fetch worklogs if all settings are set,
     * otherwise trigger the "open settings" button
     *
     * @param settings The settings object from the SettingsManager
     */
    private void handleFetchWorklogButtonClick(SettingsUtil.Settings settings) {
        LOGGER.debug("Fetch worklogs button clicked");
        if (settings.hasMissingConnectionParameters()) {
            // connection data missing hence trigger settings button click and show warning
            LOGGER.info("No settings present yet, redirecting user to settings dialogue");
            progressText.setText(resources.getString("view.main.warning.settingsblank"));
            settingsMenuItem.fire();
        } else {
            // settings present, fetch worklogs
            fetchWorklogs(settings, timerangeComboBox.getSelectionModel().getSelectedItem());
        }
    }

    /**
     * Get the converter for the ReportTimerange ComboBox
     *
     * @param resources the resource bundle to retrieve the display label from
     * @return a converter from {@link ReportTimerange} to {@link String} and back
     */
    private static StringConverter<ReportTimerange> getTimerangeComboBoxConverter(ResourceBundle resources) {
        return new StringConverter<ReportTimerange>() {
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
        };
    }

    /**
     * Opens the settings dialogue
     * @param window The main window to attach the scene to
     */
    private void showSettingsDialogue() {
        LOGGER.debug("Showing settings dialogue");
        openDialogue("/fx/views/settings.fxml", "view.settings.title", true);
    }

    private void showLogMessagesDialogue() {
        LOGGER.debug("Showing log messages dialogue");
        openDialogue("/fx/views/logMessagesView.fxml", "view.menu.help.logs", false);
    }

    private void openDialogue(String view, String titleResourceKey, boolean modal) {
        Platform.runLater(() -> {
            try {
                Parent settingsContent = FXMLLoader.load(MainViewController.class.getResource(view), resources);

                Scene settingsScene = new Scene(settingsContent);
                Stage settingsStage = new Stage();
                settingsStage.initOwner(progressBar.getScene().getWindow());

                if (modal) {
                    settingsStage.initStyle(StageStyle.UTILITY);
                    settingsStage.initModality(Modality.APPLICATION_MODAL);
                    settingsStage.setResizable(false);
                }

                settingsStage.setTitle(resources.getString(titleResourceKey));
                settingsStage.setScene(settingsScene);
                settingsStage.showAndWait();
            } catch (IOException e) {
                LOGGER.error("Could not open dialogue {}", view, e);
                throw ExceptionUtil.getRuntimeException("exceptions.view.io", e, view);
            }
        });
    }

    private void fetchWorklogs(SettingsUtil.Settings settings, ReportTimerange timerange) {
        LOGGER.debug("Fetch worklogs clicked for timerange {}", timerange.toString());
        Task<WorklogResult> worklogTaskForUser = YouTrackConnector.getInstance()
                .getWorklogTaskForUser(settings.getYoutrackUrl(), settings.getYoutrackUsername(), settings.getYoutrackPassword(), timerange);

        // disabled worklog button while still waiting for response
        worklogTaskForUser.stateProperty().addListener((observable, oldValue, newValue) -> {
            LOGGER.debug("Thread changed from {} to {}", oldValue, newValue);
            fetchWorklogButton.setDisable(newValue == Worker.State.RUNNING || newValue == Worker.State.SCHEDULED);
        });

        // error handler
        worklogTaskForUser.setOnFailed(event -> {
            Throwable throwable = event.getSource().getException();
            LOGGER.warn("Fetching worklogs failed", throwable);
            displayError(throwable);
        });

        // success handler
        worklogTaskForUser.setOnSucceeded(event -> {
            LOGGER.info("Fetching worklogs succeeded");
            WorklogResult result = (WorklogResult) event.getSource().getValue();
            displayResult(result, timerange, settings);
        });

        // bind progressbar and -text property to task
        progressText.textProperty().unbind();
        progressBar.progressProperty().unbind();
        progressText.textProperty().bind(worklogTaskForUser.messageProperty());
        progressBar.progressProperty().bind(worklogTaskForUser.progressProperty());

        // start task
        Thread thread = new Thread(worklogTaskForUser, "FetchWorklogsThread-" + timerange.name());
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Display the throwable to the user
     * @param throwable
     */
    private void displayError(Throwable throwable) {
        progressBar.progressProperty().unbind();
        progressText.textProperty().unbind();
        progressBar.progressProperty().set(0);

        if (throwable != null) {
            progressText.setText(throwable.getMessage());
            throwable.printStackTrace();
        } else {
            progressText.setText(resources.getString("exceptions.main.worker.unknown"));
        }
    }

    private void displayResult(WorklogResult result, ReportTimerange timerange, SettingsUtil.Settings settings) {
        LOGGER.info("Displaying WorklogResult to the user");
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
