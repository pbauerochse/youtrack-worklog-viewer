package de.pbauerochse.worklogviewer.fx;

import com.google.common.collect.ImmutableList;
import de.pbauerochse.worklogviewer.WorklogViewer;
import de.pbauerochse.worklogviewer.domain.Callback;
import de.pbauerochse.worklogviewer.domain.ReportTimerange;
import de.pbauerochse.worklogviewer.domain.TimerangeProvider;
import de.pbauerochse.worklogviewer.domain.timerangeprovider.TimerangeProviderFactory;
import de.pbauerochse.worklogviewer.fx.converter.GroupByCategoryStringConverter;
import de.pbauerochse.worklogviewer.fx.converter.ReportTimerangeStringConverter;
import de.pbauerochse.worklogviewer.fx.tabs.AllWorklogsTab;
import de.pbauerochse.worklogviewer.fx.tabs.OwnWorklogsTab;
import de.pbauerochse.worklogviewer.fx.tabs.ProjectWorklogTab;
import de.pbauerochse.worklogviewer.fx.tabs.WorklogTab;
import de.pbauerochse.worklogviewer.fx.tasks.ExcelExporterTask;
import de.pbauerochse.worklogviewer.fx.tasks.FetchTimereportContext;
import de.pbauerochse.worklogviewer.fx.tasks.FetchTimereportTask;
import de.pbauerochse.worklogviewer.fx.tasks.GetGroupByCategoriesTask;
import de.pbauerochse.worklogviewer.util.ExceptionUtil;
import de.pbauerochse.worklogviewer.util.FormattingUtil;
import de.pbauerochse.worklogviewer.util.SettingsUtil;
import de.pbauerochse.worklogviewer.youtrack.domain.GroupByCategory;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * @author Patrick Bauerochse
 * @since 01.04.15
 */
public class MainViewController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainViewController.class);

    private static final int AMOUNT_OF_FIXED_TABS_BEFORE_PROJECT_TABS = 2;  // two fixed tabs (own and all)
    private static final String REQUIRED_FIELD_CLASS = "required";

    @FXML
    private ComboBox<ReportTimerange> timerangeComboBox;

    @FXML
    private ComboBox<Optional<GroupByCategory>> groupByCategoryComboBox;

    @FXML
    private Button fetchWorklogButton;

    @FXML
    private MenuItem exportToExcelMenuItem;

    @FXML
    private MenuItem settingsMenuItem;

    @FXML
    private MenuItem logMessagesMenuItem;

    @FXML
    private MenuItem aboutMenuItem;

    @FXML
    private MenuItem exitMenuItem;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Text progressText;

    @FXML
    private TabPane resultTabPane;

    @FXML
    private StackPane waitScreenOverlay;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    private ResourceBundle resources;
    private SettingsUtil.Settings settings;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.debug("Initializing main view");
        this.resources = resources;

        settings = SettingsUtil.loadSettings();

        // prepopulate timerange dropdown
        timerangeComboBox.setConverter(new ReportTimerangeStringConverter());
        timerangeComboBox.getItems().addAll(ReportTimerange.values());
        timerangeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // update settings
                settings.lastUsedReportTimerangeProperty().setValue(newValue);

                // prepopulate start and end datepickers and remove error labels
                TimerangeProvider timerangeProvider = TimerangeProviderFactory.getTimerangeProvider(newValue, null, null);
                startDatePicker.setValue(timerangeProvider.getStartDate());
                endDatePicker.setValue(timerangeProvider.getEndDate());
            }
        });

        // prepopulate report timerange combobox with last used value
        timerangeComboBox.getSelectionModel().select(settings.getLastUsedReportTimerange());

        // group by combobox converter
        groupByCategoryComboBox.disableProperty().bind(groupByCategoryComboBox.itemsProperty().isNull());
        groupByCategoryComboBox.setConverter(new GroupByCategoryStringConverter(groupByCategoryComboBox));

        // start and end datepicker are only editable if report timerange is CUSTOM
        startDatePicker.disableProperty().bind(timerangeComboBox.getSelectionModel().selectedItemProperty().isNotEqualTo(ReportTimerange.CUSTOM));
        startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                startDatePicker.getStyleClass().add(REQUIRED_FIELD_CLASS);
            } else {
                startDatePicker.getStyleClass().remove(REQUIRED_FIELD_CLASS);
            }
        });
        endDatePicker.disableProperty().bind(timerangeComboBox.getSelectionModel().selectedItemProperty().isNotEqualTo(ReportTimerange.CUSTOM));
        endDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                endDatePicker.getStyleClass().add(REQUIRED_FIELD_CLASS);
            } else {
                endDatePicker.getStyleClass().remove(REQUIRED_FIELD_CLASS);
            }
        });

        // fetch worklog button click
        fetchWorklogButton.disableProperty().bind(settings.hasMissingConnectionParameters());
        fetchWorklogButton.setOnAction(clickEvent -> startFetchWorklogsTask());

        // export to excel only possible if resultTabPane is not empty and therefore seems to contain data
        exportToExcelMenuItem.disableProperty().bind(resultTabPane.getSelectionModel().selectedItemProperty().isNull());

        // menu items click actions
        exportToExcelMenuItem.setOnAction(event -> startExportToExcelTask());
        settingsMenuItem.setOnAction(event -> showSettingsDialogue());
        exitMenuItem.setOnAction(event -> WorklogViewer.getInstance().requestShutdown());
        logMessagesMenuItem.setOnAction(event -> showLogMessagesDialogue());
        aboutMenuItem.setOnAction(event -> showAboutDialogue());

        // workaround to detect whether the whole form has been rendered to screen yet
        progressBar.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == null && newValue != null) {
                onFormShown();
            }
        });

        // load group by criteria when connection parameters are present
        if (!settings.hasMissingConnectionParameters().get()) {
            startGetGroupByCategoriesTask();
        }
    }

    private void onFormShown() {
        LOGGER.debug("MainForm shown");

        if (settings.hasMissingConnectionParameters().get()) {
            LOGGER.info("No YouTrack connection settings defined yet. Opening settings dialogue");
            showSettingsDialogue();
        }

        // auto load data if a named timerange was selected
        // and the user chose to load data at startup
        if (timerangeComboBox.getSelectionModel().getSelectedItem() != ReportTimerange.CUSTOM && settings.getLoadDataAtStartup()) {
            LOGGER.debug("loadDataAtStartup set. Loading report for {}", timerangeComboBox.getSelectionModel().getSelectedItem().name());
            fetchWorklogButton.fire();
        }
    }

    /**
     * Fetches groupBy criteria from YouTrack
     */
    private void startGetGroupByCategoriesTask() {
        LOGGER.info("Fetching GroupByCategories");
        GetGroupByCategoriesTask task = new GetGroupByCategoriesTask();
        task.setOnSucceeded(event -> {
            List<GroupByCategory> categoryList = (List<GroupByCategory>) event.getSource().getValue();
            LOGGER.info("{} succeeded with {} GroupByCategories", task.getTitle(), categoryList.size());

            groupByCategoryComboBox.getItems().add(Optional.empty());
            categoryList.forEach(groupByCategory -> groupByCategoryComboBox.getItems().add(Optional.of(groupByCategory)));
            groupByCategoryComboBox.getSelectionModel().select(0);
        });

        startTask(task);
    }

    /**
     * Exports the currently visible data to an excel spreadsheet
     */
    private void startExportToExcelTask() {

        // currently visible tab
        WorklogTab tab = (WorklogTab) resultTabPane.getSelectionModel().getSelectedItem();

        // ask the user where to save the excel to
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(FormattingUtil.getFormatted("view.menu.file.exportexcel"));
        fileChooser.setInitialFileName(tab.getExcelDownloadSuggestedFilename());
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Microsoft Excel", "*.xls"));

        File targetFile = fileChooser.showSaveDialog(progressBar.getScene().getWindow());
        if (targetFile != null) {
            LOGGER.debug("Exporting tab {} to excel {}", tab.getText(), targetFile.getAbsoluteFile());

            ExcelExporterTask excelExporterTask = new ExcelExporterTask(tab, targetFile);
            excelExporterTask.setOnSucceeded(event -> {
                LOGGER.info("Excel creation succeeded");
                File result = (File) event.getSource().getValue();
                progressText.setText(FormattingUtil.getFormatted("exceptions.excel.success", result.getAbsoluteFile()));
            });

            startTask(excelExporterTask);
        }
    }

    /**
     * Fetches the worklogs for the currently defined settings from YouTrack
     */
    private void startFetchWorklogsTask() {

        // sanity checks
        LocalDate selectedStartDate = startDatePicker.getValue();
        LocalDate selectedEndDate = endDatePicker.getValue();

        if (selectedStartDate == null || selectedEndDate == null) {
            LOGGER.warn("Startdate or enddate were null");
            progressText.setText(FormattingUtil.getFormatted("exceptions.timerange.datesrequired"));
            return;
        } else if (selectedStartDate.isAfter(selectedEndDate)) {
            LOGGER.warn("Startdate was after enddate");
            progressText.setText(FormattingUtil.getFormatted("exceptions.timerange.startafterend"));
            return;
        }

        // start the task
        ReportTimerange timerange = timerangeComboBox.getSelectionModel().getSelectedItem();
        LOGGER.debug("Fetch worklogs clicked for timerange {}", timerange.toString());

        TimerangeProvider timerangeProvider = TimerangeProviderFactory.getTimerangeProvider(timerange, selectedStartDate, selectedEndDate);
        FetchTimereportContext context = new FetchTimereportContext(timerangeProvider, groupByCategoryComboBox.getSelectionModel().getSelectedItem());

        FetchTimereportTask fetchTimereportTask = new FetchTimereportTask(context);

        // success handler
        fetchTimereportTask.setOnSucceeded(event -> {
            LOGGER.info("Fetching worklogs succeeded");
            displayWorklogResult(context, settings);
        });

        startTask(fetchTimereportTask);
    }

    /**
     * Starts a thread performing the given task
     * @param task The task to perform
     */
    private void startTask(Task task) {
        LOGGER.info("Starting task {}", task.getTitle());
        waitScreenOverlay.setVisible(true);

        // success handler
        EventHandler<WorkerStateEvent> onSucceededEventHandler = task.getOnSucceeded();
        task.setOnSucceeded(event -> {
            LOGGER.info("Task {} succeeded", task.getTitle());
            WorkerStateEvent asWorkerstateEvent = (WorkerStateEvent) event; // stupid compiler sometimes gets confused in lambdas

            // unbind progress indicators
            progressText.textProperty().unbind();
            progressBar.progressProperty().unbind();

            if (onSucceededEventHandler != null) {
                LOGGER.debug("Delegating Event to previous onSucceeded event handler");
                onSucceededEventHandler.handle(asWorkerstateEvent);
            }

            waitScreenOverlay.setVisible(false);
        });

        // error handler
        EventHandler<WorkerStateEvent> onFailedEventHandler = task.getOnFailed();
        task.setOnFailed(event -> {
            LOGGER.warn("Task {} failed", task.getTitle());
            WorkerStateEvent asWorkerstateEvent = (WorkerStateEvent) event; // stupid compiler sometimes gets confused in lambdas

            // unbind progress indicators
            progressText.textProperty().unbind();
            progressBar.progressProperty().unbind();

            if (onFailedEventHandler != null) {
                LOGGER.debug("Delegating Event to previous onFailed event handler");
                onFailedEventHandler.handle(asWorkerstateEvent);
            }

            Throwable throwable = asWorkerstateEvent.getSource().getException();
            if (throwable != null) {
                LOGGER.warn("Showing error to user", throwable);
                progressText.setText(throwable.getMessage());
            } else {
                progressText.setText(FormattingUtil.getFormatted("exceptions.main.worker.unknown"));
            }

            waitScreenOverlay.setVisible(false);
        });

        // state change listener just for logging purposes
        task.stateProperty().addListener((observable, oldValue, newValue) -> LOGGER.debug("Task {} changed from {} to {}", task.getTitle(), oldValue, newValue));

        // bind progress indicators
        progressText.textProperty().unbind();
        progressBar.progressProperty().unbind();

        progressText.textProperty().bind(task.messageProperty());
        progressBar.progressProperty().bind(task.progressProperty());

        // start the task in a thread
        Thread thread = new Thread(task, task.getTitle());
        thread.setDaemon(true);
        thread.start();
    }

    private void displayWorklogResult(FetchTimereportContext context, SettingsUtil.Settings settings) {
        LOGGER.info("Displaying WorklogResult to the user");

        if (resultTabPane.getTabs().size() == 0) {
            LOGGER.debug("Adding default tabs");
            resultTabPane.getTabs().add(new OwnWorklogsTab());
        }

        if (settings.getShowAllWorklogs()) {

            if (resultTabPane.getTabs().size() < 2 || !(resultTabPane.getTabs().get(1) instanceof AllWorklogsTab)) {
                resultTabPane.getTabs().add(new AllWorklogsTab());
            }

            ImmutableList<String> distinctProjectNames = context.getResult().get().getDistinctProjectNames();
            for (int i = 0; i < distinctProjectNames.size(); i++) {
                int tabIndex = AMOUNT_OF_FIXED_TABS_BEFORE_PROJECT_TABS + i;

                String newTabLabel = distinctProjectNames.get(i);
                WorklogTab tab;
                if (resultTabPane.getTabs().size() > tabIndex) {
                    // there is a tab we can reuse
                    tab = (WorklogTab) resultTabPane.getTabs().get(tabIndex);
                    LOGGER.debug("Reusing Tab {} for project {}", tab.getText(), newTabLabel);
                } else {
                    LOGGER.debug("Adding new project tab for project {}", newTabLabel);
                    tab = new ProjectWorklogTab(newTabLabel);
                    resultTabPane.getTabs().add(tab);
                }

                tab.setText(newTabLabel);
            }

            // remove any redundant tabs
            for (int tabIndexToRemove = distinctProjectNames.size() + AMOUNT_OF_FIXED_TABS_BEFORE_PROJECT_TABS; tabIndexToRemove < resultTabPane.getTabs().size(); tabIndexToRemove++) {
                WorklogTab removedTab = (WorklogTab) resultTabPane.getTabs().remove(tabIndexToRemove);
                LOGGER.debug("Removing tab at index {}: {}", tabIndexToRemove, removedTab.getText());
            }
        } else if (resultTabPane.getTabs().size() > 1) {
            // remove all other tabs when settings changed from showAll to showOnlyOwnWorklogs
            LOGGER.debug("Removing all and project tabs since user switched to showOnlyOwnWorklogs mode");
            resultTabPane.getTabs().remove(1, resultTabPane.getTabs().size());
            resultTabPane.getSelectionModel().select(0);
        }

        resultTabPane.getTabs().forEach(tab -> ((WorklogTab) tab).updateItems(context));
    }

    private void showSettingsDialogue() {
        LOGGER.debug("Showing settings dialogue");

        // pass in a handler to fetch the group by categories if connection
        // parameters get set
        openDialogue("/fx/views/settings.fxml", "view.settings.title", true, Optional.of(() -> {
            if (!settings.hasMissingConnectionParameters().get() && groupByCategoryComboBox.getItems().size() == 0) {
                LOGGER.debug("Settings window closed, connection settings set and groupBy combobox empty -> trying to fetch groupByCategories");
                startGetGroupByCategoriesTask();
            }
        }));
    }

    private void showLogMessagesDialogue() {
        LOGGER.debug("Showing log messages dialogue");
        openDialogue("/fx/views/logMessagesView.fxml", "view.menu.help.logs", false);
    }

    private void showAboutDialogue() {
        LOGGER.debug("Showing log messages dialogue");
        openDialogue("/fx/views/about.fxml", "view.menu.help.about", true);
    }

    private void openDialogue(String view, String titleResourceKey, boolean modal) {
        openDialogue(view, titleResourceKey, modal, Optional.empty());
    }

    private void openDialogue(String view, String titleResourceKey, boolean modal, Optional<Callback> onCloseCallback) {
        try {
            Parent content = FXMLLoader.load(MainViewController.class.getResource(view), resources);

            Scene scene = new Scene(content);
            Stage stage = new Stage();
            stage.initOwner(progressBar.getScene().getWindow());

            if (modal) {
                stage.initStyle(StageStyle.UTILITY);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setResizable(false);
            }

            stage.setTitle(FormattingUtil.getFormatted(titleResourceKey));
            stage.setScene(scene);

            if (onCloseCallback.isPresent()) {
                stage.setOnCloseRequest(event -> {
                    LOGGER.debug("View {} got close request. Notifying callback", view);
                    onCloseCallback.get().invoke();
                });
            }

            stage.showAndWait();
        } catch (IOException e) {
            LOGGER.error("Could not open dialogue {}", view, e);
            throw ExceptionUtil.getRuntimeException("exceptions.view.io", e, view);
        }
    }
}
