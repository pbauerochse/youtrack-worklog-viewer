package de.pbauerochse.youtrack.fx;

import de.pbauerochse.youtrack.WorklogViewer;
import de.pbauerochse.youtrack.domain.ReportTimerange;
import de.pbauerochse.youtrack.domain.TimerangeProvider;
import de.pbauerochse.youtrack.domain.WorklogResult;
import de.pbauerochse.youtrack.domain.timerangeprovider.TimerangeProviderFactory;
import de.pbauerochse.youtrack.fx.tabs.AllWorklogsTab;
import de.pbauerochse.youtrack.fx.tabs.OwnWorklogsTab;
import de.pbauerochse.youtrack.fx.tabs.ProjectWorklogTab;
import de.pbauerochse.youtrack.fx.tabs.WorklogTab;
import de.pbauerochse.youtrack.fx.tasks.ExcelExporterTask;
import de.pbauerochse.youtrack.fx.tasks.FetchTimereportContext;
import de.pbauerochse.youtrack.fx.tasks.FetchTimereportTask;
import de.pbauerochse.youtrack.util.ExceptionUtil;
import de.pbauerochse.youtrack.util.FormattingUtil;
import de.pbauerochse.youtrack.util.SettingsUtil;
import javafx.application.Platform;
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
import javafx.util.StringConverter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Patrick Bauerochse
 * @since 01.04.15
 */
public class MainViewController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainViewController.class);

    private static final int AMOUNT_OF_FIXED_TABS_BEFORE_PROJECT_TABS = 2;  // two fixed tabs (own and all)
    public static final String REQUIRED_FIELD_CLASS = "required";

    @FXML
    private ComboBox<ReportTimerange> timerangeComboBox;

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
    private StackPane modalOverlaySpinner;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    private ResourceBundle resources;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.debug("Initializing main view");
        this.resources = resources;
        SettingsUtil.Settings settings = SettingsUtil.loadSettings();

        modalOverlaySpinner.setVisible(false);

        startDatePicker.disableProperty().bind(timerangeComboBox.getSelectionModel().selectedItemProperty().isNotEqualTo(ReportTimerange.CUSTOM));
        endDatePicker.disableProperty().bind(timerangeComboBox.getSelectionModel().selectedItemProperty().isNotEqualTo(ReportTimerange.CUSTOM));

        exportToExcelMenuItem.disableProperty().bind(resultTabPane.getSelectionModel().selectedItemProperty().isNull());
        exportToExcelMenuItem.setOnAction(event -> startExportToExcelTask((WorklogTab) resultTabPane.getSelectionModel().getSelectedItem()));

        // prepopulate timerange dropdown
        timerangeComboBox.setConverter(getTimerangeComboBoxConverter());
        timerangeComboBox.getItems().addAll(ReportTimerange.values());
        timerangeComboBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                ReportTimerange timerange = timerangeComboBox.getSelectionModel().getSelectedItem();
                settings.setLastUsedReportTimerange(timerange);

                TimerangeProvider timerangeProvider = TimerangeProviderFactory.getTimerangeProvider(timerange, null, null);
                startDatePicker.setValue(timerangeProvider.getStartDate());
                endDatePicker.setValue(timerangeProvider.getEndDate());

                startDatePicker.getStyleClass().remove(REQUIRED_FIELD_CLASS);
                endDatePicker.getStyleClass().remove(REQUIRED_FIELD_CLASS);
            }
        });

        if (settings.getLastUsedReportTimerange() != null) {
            timerangeComboBox.getSelectionModel().select(settings.getLastUsedReportTimerange());
        } else {
            timerangeComboBox.getSelectionModel().select(ReportTimerange.THIS_WEEK);    // preselect "this week"
        }

        // menu items click
        settingsMenuItem.setOnAction(event -> showSettingsDialogue());
        exitMenuItem.setOnAction(event -> WorklogViewer.getInstance().requestShutdown());
        logMessagesMenuItem.setOnAction(event -> showLogMessagesDialogue());
        aboutMenuItem.setOnAction(event -> showAboutDialogue());

        // fetch worklog button click
        fetchWorklogButton.setOnAction(clickEvent -> handleFetchWorklogButtonClick(settings));

        // auto load data if a named timerange was selected
        // and the user chose to load data at startup
        if (timerangeComboBox.getSelectionModel().getSelectedItem() != ReportTimerange.CUSTOM && settings.isLoadDataAtStartup()) {
            LOGGER.debug("loadDataAtStartup set. Loading report for {}", timerangeComboBox.getSelectionModel().getSelectedItem().name());
            fetchWorklogButton.fire();
        }
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
            progressText.setText(FormattingUtil.getFormatted("view.main.warning.settingsblank"));
            settingsMenuItem.fire();
        } else {
            // settings present, fetch worklogs
            fetchWorklogs(settings, timerangeComboBox.getSelectionModel().getSelectedItem());
        }
    }

    /**
     * Get the converter for the ReportTimerange ComboBox
     *
     * @return a converter from {@link ReportTimerange} to {@link String} and back
     */
    private static StringConverter<ReportTimerange> getTimerangeComboBoxConverter() {
        return new StringConverter<ReportTimerange>() {
            @Override
            public String toString(ReportTimerange object) {
                return FormattingUtil.getFormatted(object.getLabelKey());
            }

            @Override
            public ReportTimerange fromString(String string) {
                for (ReportTimerange timerange : ReportTimerange.values()) {
                    if (StringUtils.equals(FormattingUtil.getFormatted(timerange.getLabelKey()), string)) {
                        return timerange;
                    }
                }
                return null;
            }
        };
    }

    /**
     * Opens the settings dialogue
     */
    private void showSettingsDialogue() {
        LOGGER.debug("Showing settings dialogue");
        openDialogue("/fx/views/settings.fxml", "view.settings.title", true);
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

                settingsStage.setTitle(FormattingUtil.getFormatted(titleResourceKey));
                settingsStage.setScene(settingsScene);
                settingsStage.showAndWait();
            } catch (IOException e) {
                LOGGER.error("Could not open dialogue {}", view, e);
                throw ExceptionUtil.getRuntimeException("exceptions.view.io", e, view);
            }
        });
    }

    private void startExportToExcelTask(WorklogTab tab) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(FormattingUtil.getFormatted("view.menu.file.exportexcel"));
        fileChooser.setInitialFileName(tab.getExcelDownloadSuggestedFilename());
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Microsoft Excel", "*.xls"));

        File targetFile = fileChooser.showSaveDialog(progressBar.getScene().getWindow());
        if (targetFile != null) {
            LOGGER.debug("Exporting tab {} to excel {}", tab.getText(), targetFile.getAbsoluteFile());
            showWaitScreen();

            ExcelExporterTask excelExporterTask = new ExcelExporterTask(tab, targetFile);

            excelExporterTask.stateProperty().addListener((observable, oldValue, newValue) -> {
                LOGGER.debug("Thread changed from {} to {}", oldValue, newValue);
            });

            // error handler
            excelExporterTask.setOnFailed(event -> {
                Throwable throwable = event.getSource().getException();
                LOGGER.warn("Creating excel failed", throwable);
                displayError(throwable);
                hideWaitScreen();
            });

            // success handler
            excelExporterTask.setOnSucceeded(event -> {
                LOGGER.info("Excel creation succeeded");
                File result = (File) event.getSource().getValue();
                progressText.textProperty().unbind();
                progressBar.progressProperty().unbind();
                progressText.setText(FormattingUtil.getFormatted("exceptions.excel.success", result.getAbsoluteFile()));
                hideWaitScreen();
            });

            // bind progressbar and -text property to task
            progressText.textProperty().unbind();
            progressBar.progressProperty().unbind();
            progressText.textProperty().bind(excelExporterTask.messageProperty());
            progressBar.progressProperty().bind(excelExporterTask.progressProperty());

            // start task
            Thread thread = new Thread(excelExporterTask, "ExcelReportGenerator");
            thread.setDaemon(true);
            thread.start();
        }
    }

    private void fetchWorklogs(SettingsUtil.Settings settings, ReportTimerange timerange) {
        LOGGER.debug("Fetch worklogs clicked for timerange {}", timerange.toString());

        progressText.textProperty().unbind();
        progressBar.progressProperty().unbind();

        if (startDatePicker.getValue() == null) {
            startDatePicker.getStyleClass().add(REQUIRED_FIELD_CLASS);
        } else {
            startDatePicker.getStyleClass().remove(REQUIRED_FIELD_CLASS);
        }

        if (endDatePicker.getValue() == null) {
            endDatePicker.getStyleClass().add(REQUIRED_FIELD_CLASS);
        } else {
            endDatePicker.getStyleClass().remove(REQUIRED_FIELD_CLASS);
        }

        // sanity checks if ReportTimerange == CUSTOM
        if (timerange == ReportTimerange.CUSTOM && (startDatePicker.getValue() == null || endDatePicker.getValue() == null)) {
            progressText.setText(FormattingUtil.getFormatted("exceptions.customrange.datesrequired"));
            return;
        } else if (startDatePicker.getValue().isAfter(endDatePicker.getValue())) {
            progressText.setText(FormattingUtil.getFormatted("exceptions.customrange.startafterend"));
            return;
        }

        showWaitScreen();

        TimerangeProvider timerangeProvider = TimerangeProviderFactory.getTimerangeProvider(timerange, startDatePicker.getValue(), endDatePicker.getValue());
        FetchTimereportContext context = new FetchTimereportContext(timerangeProvider);
        FetchTimereportTask worklogTaskForUser = new FetchTimereportTask(context);

        worklogTaskForUser.stateProperty().addListener((observable, oldValue, newValue) -> {
            LOGGER.debug("Thread changed from {} to {}", oldValue, newValue);
        });

        // error handler
        worklogTaskForUser.setOnFailed(event -> {
            Throwable throwable = event.getSource().getException();
            LOGGER.warn("Fetching worklogs failed", throwable);
            displayError(throwable);
            hideWaitScreen();
        });

        // success handler
        worklogTaskForUser.setOnSucceeded(event -> {
            LOGGER.info("Fetching worklogs succeeded");
            WorklogResult result = (WorklogResult) event.getSource().getValue();
            displayResult(result, context, settings);
            hideWaitScreen();
        });

        // bind progressbar and -text property to task
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
            LOGGER.warn("Showing error to user", throwable);
        } else {
            progressText.setText(FormattingUtil.getFormatted("exceptions.main.worker.unknown"));
        }
    }

    private void displayResult(WorklogResult result, FetchTimereportContext context, SettingsUtil.Settings settings) {
        LOGGER.info("Displaying WorklogResult to the user");

        if (resultTabPane.getTabs().size() == 0) {
            LOGGER.debug("Adding default tabs");
            resultTabPane.getTabs().add(new OwnWorklogsTab());
        }

        if (settings.isShowAllWorklogs()) {

            if (resultTabPane.getTabs().size() < 2 || !(resultTabPane.getTabs().get(1) instanceof AllWorklogsTab)) {
                resultTabPane.getTabs().add(new AllWorklogsTab());
            }

            for (int i = 0; i < result.getDistinctProjectNames().size(); i++) {
                int tabIndex = AMOUNT_OF_FIXED_TABS_BEFORE_PROJECT_TABS + i;

                String newTabLabel = result.getDistinctProjectNames().get(i);
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
            for (int tabIndexToRemove = result.getDistinctProjectNames().size() + AMOUNT_OF_FIXED_TABS_BEFORE_PROJECT_TABS; tabIndexToRemove < resultTabPane.getTabs().size(); tabIndexToRemove++) {
                WorklogTab removedTab = (WorklogTab) resultTabPane.getTabs().remove(tabIndexToRemove);
                LOGGER.debug("Removing tab at index {}: {}", tabIndexToRemove, removedTab.getText());
            }
        } else if (resultTabPane.getTabs().size() > 1) {
            // remove all other tabs when settings changed from showAll to showOnlyOwnWorklogs
            LOGGER.debug("Removing all and project tabs since user switched to showOnlyOwnWorklogs mode");
            resultTabPane.getTabs().remove(1, resultTabPane.getTabs().size());
            resultTabPane.getSelectionModel().select(0);
        }

        resultTabPane.getTabs().forEach(tab -> ((WorklogTab) tab).updateItems(result, context));
    }

    private void showWaitScreen() {
        modalOverlaySpinner.setVisible(true);
    }

    private void hideWaitScreen() {
        modalOverlaySpinner.setVisible(false);
    }
}
