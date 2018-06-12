package de.pbauerochse.worklogviewer.fx;

import de.pbauerochse.worklogviewer.WorklogViewer;
import de.pbauerochse.worklogviewer.domain.Callback;
import de.pbauerochse.worklogviewer.domain.ReportTimerange;
import de.pbauerochse.worklogviewer.domain.TimerangeProvider;
import de.pbauerochse.worklogviewer.domain.timerangeprovider.TimerangeProviderFactory;
import de.pbauerochse.worklogviewer.fx.components.WorklogTab;
import de.pbauerochse.worklogviewer.fx.components.tabs.TimeReportResultTabbedPane;
import de.pbauerochse.worklogviewer.fx.converter.GroupByCategoryStringConverter;
import de.pbauerochse.worklogviewer.fx.converter.ReportTimerangeStringConverter;
import de.pbauerochse.worklogviewer.fx.tasks.ExcelExporterTask;
import de.pbauerochse.worklogviewer.fx.tasks.FetchTimereportTask;
import de.pbauerochse.worklogviewer.fx.tasks.GetGroupByCategoriesTask;
import de.pbauerochse.worklogviewer.fx.tasks.VersionCheckerTask;
import de.pbauerochse.worklogviewer.settings.SettingsUtil;
import de.pbauerochse.worklogviewer.settings.SettingsViewModel;
import de.pbauerochse.worklogviewer.util.ExceptionUtil;
import de.pbauerochse.worklogviewer.util.FormattingUtil;
import de.pbauerochse.worklogviewer.util.HyperlinkUtil;
import de.pbauerochse.worklogviewer.version.GitHubVersion;
import de.pbauerochse.worklogviewer.version.Version;
import de.pbauerochse.worklogviewer.youtrack.TimeReport;
import de.pbauerochse.worklogviewer.youtrack.TimeReportParameters;
import de.pbauerochse.worklogviewer.youtrack.domain.GroupByCategory;
import de.pbauerochse.worklogviewer.youtrack.domain.NoSelectionGroupByCategory;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
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
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Java FX Controller for the main window
 */
public class MainViewController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainViewController.class);

    private static final int AMOUNT_OF_FIXED_TABS_BEFORE_PROJECT_TABS = 2;  // two fixed components (own and all)
    private static final String REQUIRED_FIELD_CLASS = "required";

    public static ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(1, 1, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<>());

    @FXML
    private ComboBox<ReportTimerange> timerangeComboBox;

    @FXML
    private ComboBox<GroupByCategory> groupByCategoryComboBox;

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
    private TimeReportResultTabbedPane resultTabPane;

    @FXML
    private StackPane waitScreenOverlay;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private ToolBar mainToolbar;

    private ResourceBundle resources;
    private SettingsViewModel settingsModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.debug("Initializing main view");
        this.resources = resources;
        this.settingsModel = SettingsUtil.getSettingsViewModel();

        initializeTimerangeComboBox();
        initializeGroupByComboBox();
        initializeDatePickers();
        initializeFetchWorklogsButton();
        initializeMenuItems();

        checkForUpdate();

        // workaround to detect whether the whole form has been rendered to screen yet
        progressBar.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue == null && newValue != null) {
                onFormShown();
            }
        });

    }

    private void initializeTimerangeComboBox() {
        timerangeComboBox.setConverter(new ReportTimerangeStringConverter());
        timerangeComboBox.getItems().addAll(ReportTimerange.values());
        timerangeComboBox.getSelectionModel().select(settingsModel.lastUsedReportTimerangeProperty().get());
        timerangeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> timerangeChanged(newValue));

        settingsModel.lastUsedReportTimerangeProperty().addListener((observable, oldValue, newValue) -> timerangeComboBox.getSelectionModel().select(newValue));
    }

    private void timerangeChanged(@NotNull ReportTimerange newValue) {
        // prepopulate start and end datepickers and remove error labels
        TimerangeProvider timerangeProvider = TimerangeProviderFactory.getTimerangeProvider(newValue, null, null);
        startDatePicker.setValue(timerangeProvider.getStartDate());
        endDatePicker.setValue(timerangeProvider.getEndDate());
    }

    private void initializeGroupByComboBox() {
        groupByCategoryComboBox.disableProperty().bind(groupByCategoryComboBox.itemsProperty().isNull());
        groupByCategoryComboBox.setConverter(new GroupByCategoryStringConverter(groupByCategoryComboBox));

        // load group by criteria when connection parameters are present
        if (!settingsModel.getHasMissingConnectionSettings()) {
            startGetGroupByCategoriesTask();
        }
    }

    /**
     * Fetches groupBy criteria from YouTrack
     */
    @SuppressWarnings("unchecked")
    private void startGetGroupByCategoriesTask() {
        LOGGER.info("Fetching GroupByCategories");
        GetGroupByCategoriesTask task = new GetGroupByCategoriesTask();
        task.setOnSucceeded(event -> {
            Worker<List<GroupByCategory>> worker = event.getSource();
            List<GroupByCategory> categoryList = worker.getValue();
            LOGGER.info("{} succeeded with {} GroupByCategories", task.getTitle(), categoryList.size());

            groupByCategoryComboBox.getItems().add(new NoSelectionGroupByCategory());
            groupByCategoryComboBox.getItems().addAll(categoryList.stream().sorted(Comparator.comparing(GroupByCategory::getName)).collect(Collectors.toList()));
            groupByCategoryComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                String lastUsed = newValue == null ? null : newValue.getId();
                settingsModel.lastUsedGroupByCategoryIdProperty().setValue(lastUsed);
            });

            String lastUsedGroupById = settingsModel.getLastUsedGroupByCategoryId();
            int selectedItemIndex = 0;

            for (int i = 0; i < groupByCategoryComboBox.getItems().size(); i++) {
                if (StringUtils.equals(groupByCategoryComboBox.getItems().get(i).getId(), lastUsedGroupById)) {
                    selectedItemIndex = i;
                    break;
                }
            }

            groupByCategoryComboBox.getSelectionModel().select(selectedItemIndex);
        });
        startTask(task);
    }

    private void initializeDatePickers() {
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
    }

    private void initializeFetchWorklogsButton() {
        // fetch worklog button click
        fetchWorklogButton.disableProperty().bind(settingsModel.hasMissingConnectionSettingsProperty());
        fetchWorklogButton.setOnAction(event -> fetchWorklogs());
    }

    private void initializeMenuItems() {
        // export to excel only possible if resultTabPane is not empty and therefore seems to contain data
        exportToExcelMenuItem.disableProperty().bind(resultTabPane.getSelectionModel().selectedItemProperty().isNull());

        // menu items click actions
        exportToExcelMenuItem.setOnAction(event -> startExportToExcelTask());

        settingsMenuItem.setOnAction(event -> showSettingsDialogue());
        exitMenuItem.setOnAction(event -> WorklogViewer.getInstance().requestShutdown());
        logMessagesMenuItem.setOnAction(event -> showLogMessagesDialogue());
        aboutMenuItem.setOnAction(event -> showAboutDialogue());
    }

    private void checkForUpdate() {
        VersionCheckerTask versionCheckTask = new VersionCheckerTask();
        versionCheckTask.setOnSucceeded(this::addDownloadLinkToToolbarIfNeverVersionPresent);
        startTask(versionCheckTask);
    }

    private void addDownloadLinkToToolbarIfNeverVersionPresent(WorkerStateEvent event) {
        Optional<GitHubVersion> gitHubVersionOptional = ((VersionCheckerTask) event.getSource()).getValue();
        gitHubVersionOptional.ifPresent(gitHubVersion -> {
            Version currentVersion = new Version(resources.getString("release.version"));
            Version mostRecentVersion = new Version(gitHubVersion.getVersion());

            LOGGER.debug("Most recent github version is {}, this version is {}", mostRecentVersion, currentVersion);
            if (mostRecentVersion.isNewerThan(currentVersion)) {
                Hyperlink link = HyperlinkUtil.createLink(
                        FormattingUtil.getFormatted("worker.updatecheck.available", mostRecentVersion.toString()),
                        gitHubVersion.getUrl()
                );
                mainToolbar.getItems().add(link);
            }
        });
    }

    private void onFormShown() {
        LOGGER.debug("MainForm shown");

        if (settingsModel.getHasMissingConnectionSettings()) {
            LOGGER.info("No YouTrack connection settings defined yet. Opening settings dialogue");
            showSettingsDialogue();
        }

        // auto load data if a named timerange was selected
        // and the user chose to load data at startup
        if (timerangeComboBox.getSelectionModel().getSelectedItem() != ReportTimerange.CUSTOM && settingsModel.loadDataAtStartupProperty().get()) {
            LOGGER.debug("loadDataAtStartup set. Loading report for {}", timerangeComboBox.getSelectionModel().getSelectedItem().name());
            fetchWorklogButton.fire();
        }
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
//        fileChooser.setInitialFileName(tab.getExcelDownloadSuggestedFilename());
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
    private void fetchWorklogs() {
        ReportTimerange timerange = timerangeComboBox.getSelectionModel().getSelectedItem();
        LOGGER.debug("Fetch worklogs clicked for timerange {}", timerange.toString());

        LocalDate selectedStartDate = startDatePicker.getValue();
        LocalDate selectedEndDate = endDatePicker.getValue();

        TimerangeProvider timerangeProvider = TimerangeProviderFactory.getTimerangeProvider(timerange, selectedStartDate, selectedEndDate);


        GroupByCategory groupByCategory = groupByCategoryComboBox.getSelectionModel().getSelectedItem();

        TimeReportParameters parameters = new TimeReportParameters(timerangeProvider, groupByCategory);

        FetchTimereportTask task = new FetchTimereportTask(parameters);
        task.setOnSucceeded(event -> displayWorklogResult((TimeReport) event.getSource().getValue()));
        startTask(task);
    }

    /**
     * Starts a thread performing the given task
     * @param task The task to perform
     */
    private void startTask(Task task) {
        LOGGER.info("Starting task {}", task.getTitle());
        EventHandler onRunningEventHandler = task.getOnRunning();
        task.setOnRunning(event -> {
            waitScreenOverlay.setVisible(true);
            progressText.textProperty().bind(task.messageProperty());
            progressBar.progressProperty().bind(task.progressProperty());

            if (onRunningEventHandler != null) {
                onRunningEventHandler.handle(event);
            }
        });

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
            if (throwable != null && StringUtils.isNotBlank(throwable.getMessage())) {
                LOGGER.warn("Showing error to user", throwable);
                progressText.setText(throwable.getMessage());
            } else {
                if (throwable != null) {
                    LOGGER.warn("Error executing task {}", task.toString(), throwable);
                }

                progressText.setText(FormattingUtil.getFormatted("exceptions.main.worker.unknown"));
            }

            progressBar.setProgress(1);
            waitScreenOverlay.setVisible(false);
        });

        // state change listener just for logging purposes
        task.stateProperty().addListener((observable, oldValue, newValue) -> LOGGER.debug("Task {} changed from {} to {}", task.getTitle(), oldValue, newValue));

        EXECUTOR.submit(task);
    }

    private void displayWorklogResult(TimeReport timeReport) {
        LOGGER.info("Presenting TimeReport to the user");
        resultTabPane.update(timeReport);


//        if (resultTabPane.getTabs().size() == 0) {
//            LOGGER.debug("Adding default components");
//            resultTabPane.getTabs().add(new OwnWorklogsTab());
//        }
//        if (settings.isShowAllWorklogs()) {
//
//            if (resultTabPane.getTabs().size() < 2 || !(resultTabPane.getTabs().get(1) instanceof AllWorklogsTab)) {
//                resultTabPane.getTabs().add(new AllWorklogsTab());
//            }
//
//            ImmutableList<String> distinctProjectNames = context.getResult().get().getDistinctProjectNames();
//            for (int i = 0; i < distinctProjectNames.size(); i++) {
//                int tabIndex = AMOUNT_OF_FIXED_TABS_BEFORE_PROJECT_TABS + i;
//
//                String newTabLabel = distinctProjectNames.get(i);
//                WorklogTab tab;
//                if (resultTabPane.getTabs().size() > tabIndex) {
//                    // there is a tab we can reuse
//                    tab = (WorklogTab) resultTabPane.getTabs().get(tabIndex);
//                    LOGGER.debug("Reusing Tab {} for project {}", tab.getText(), newTabLabel);
//                } else {
//                    LOGGER.debug("Adding new project tab for project {}", newTabLabel);
//                    tab = new ProjectWorklogTab(newTabLabel);
//                    resultTabPane.getTabs().add(tab);
//                }
//
//                tab.setText(newTabLabel);
//            }
//
//            // remove any redundant components
//            for (int tabIndexToRemove = distinctProjectNames.size() + AMOUNT_OF_FIXED_TABS_BEFORE_PROJECT_TABS; tabIndexToRemove < resultTabPane.getTabs().size(); tabIndexToRemove++) {
//                WorklogTab removedTab = (WorklogTab) resultTabPane.getTabs().remove(tabIndexToRemove);
//                LOGGER.debug("Removing tab at index {}: {}", tabIndexToRemove, removedTab.getText());
//            }
//        } else if (resultTabPane.getTabs().size() > 1) {
//            // remove all other components when settings changed from showAll to showOnlyOwnWorklogs
//            LOGGER.debug("Removing all and project components since user switched to showOnlyOwnWorklogs mode");
//            resultTabPane.getTabs().remove(1, resultTabPane.getTabs().size());
//            resultTabPane.getSelectionModel().select(0);
//        }
//
//        resultTabPane.getTabs().forEach(tab -> ((WorklogTab) tab).updateItems(context));
    }

    private void showSettingsDialogue() {
        LOGGER.debug("Showing settings dialogue");

        // pass in a handler to fetch the group by categories if connection
        // parameters get set
        openDialogue("/fx/views/settings.fxml", "view.settings.title", true, () -> {
            if (!settingsModel.getHasMissingConnectionSettings() && groupByCategoryComboBox.getItems().size() == 0) {
                LOGGER.debug("Settings window closed, connection settings set and groupBy combobox empty -> trying to fetch groupByCategories");
                startGetGroupByCategoriesTask();
            }
        });
    }

    private void showLogMessagesDialogue() {
        LOGGER.debug("Showing log messages dialogue");
        openDialogue("/fx/views/logMessagesView.fxml", "view.menu.help.logs");
    }

    private void showAboutDialogue() {
        LOGGER.debug("Showing log messages dialogue");
        openDialogue("/fx/views/about.fxml", "view.menu.help.about");
    }

    private void openDialogue(String view, String titleResourceKey) {
        openDialogue(view, titleResourceKey, false, null);
    }

    private void openDialogue(String view, String titleResourceKey, boolean modal, Callback onCloseCallback) {
        try {
            Parent content = FXMLLoader.load(MainViewController.class.getResource(view), resources);

            Scene scene = new Scene(content);
            scene.getStylesheets().add("/fx/css/base-styling.css");
            Stage stage = new Stage();
            stage.initOwner(progressBar.getScene().getWindow());

            if (modal) {
                stage.initStyle(StageStyle.UTILITY);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setResizable(false);
            }

            stage.setTitle(FormattingUtil.getFormatted(titleResourceKey));
            stage.setScene(scene);

            if (onCloseCallback != null) {
                stage.setOnCloseRequest(event -> {
                    LOGGER.debug("View {} got close request. Notifying callback", view);
                    onCloseCallback.invoke();
                });
            }

            stage.showAndWait();
        } catch (IOException e) {
            LOGGER.error("Could not open dialogue {}", view, e);
            throw ExceptionUtil.getRuntimeException("exceptions.view.io", e, view);
        }
    }
}
