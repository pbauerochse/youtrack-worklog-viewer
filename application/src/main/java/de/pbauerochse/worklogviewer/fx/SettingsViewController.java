package de.pbauerochse.worklogviewer.fx;

import de.pbauerochse.worklogviewer.WorklogViewer;
import de.pbauerochse.worklogviewer.connector.YouTrackConnectorLocator;
import de.pbauerochse.worklogviewer.connector.YouTrackVersion;
import de.pbauerochse.worklogviewer.connector.v2018.SupportedVersions;
import de.pbauerochse.worklogviewer.fx.converter.YouTrackVersionStringConverter;
import de.pbauerochse.worklogviewer.settings.SettingsUtil;
import de.pbauerochse.worklogviewer.settings.SettingsViewModel;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

/**
 * FX Controller for the settings
 * dialog
 */
public class SettingsViewController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsViewController.class);

    @FXML
    private TextField youtrackUrlField;

    @FXML
    private ComboBox<YouTrackVersion> youtrackVersionField;

    @FXML
    private TextField youtrackUsernameField;

    @FXML
    private PasswordField youtrackPermanentTokenField;

    @FXML
    private ComboBox<Integer> workhoursComboBox;

    @FXML
    private ComboBox<Theme> themeComboBox;

    @FXML
    private CheckBox showAllWorklogsCheckBox;

    @FXML
    private CheckBox showStatisticsCheckBox;

    @FXML
    private CheckBox loadDataAtStartupCheckBox;

    @FXML
    private CheckBox showDecimalsInExcel;

    @FXML
    private Button saveSettingsButton;

    @FXML
    private Button cancelSettingsButton;

    @FXML
    private CheckBox mondayCollapseCheckbox;
    @FXML
    private CheckBox tuesdayCollapseCheckbox;
    @FXML
    private CheckBox wednesdayCollapseCheckbox;
    @FXML
    private CheckBox thursdayCollapseCheckbox;
    @FXML
    private CheckBox fridayCollapseCheckbox;
    @FXML
    private CheckBox saturdayCollapseCheckbox;
    @FXML
    private CheckBox sundayCollapseCheckbox;

    @FXML
    private CheckBox mondayHighlightCheckbox;
    @FXML
    private CheckBox tuesdayHighlightCheckbox;
    @FXML
    private CheckBox wednesdayHighlightCheckbox;
    @FXML
    private CheckBox thursdayHighlightCheckbox;
    @FXML
    private CheckBox fridayHighlightCheckbox;
    @FXML
    private CheckBox saturdayHighlightCheckbox;
    @FXML
    private CheckBox sundayHighlightCheckbox;

    // TODO remove once https://youtrack.jetbrains.com/oauth?state=%2Fissue%2FJT-47943 is published
    @FXML
    private Label youtrackWorklogFielNameLabel;

    @FXML
    private TextField youtrackWorklogFielNameField;

    @FXML
    private Hyperlink youtrackWorklogFielNameHelpLink;

    @FXML
    private Label youtrackWorklogFielNameHint;

    private ResourceBundle resourceBundle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.debug("Initializing");
        this.resourceBundle = resources;

        SettingsViewModel viewModel = SettingsUtil.getSettingsViewModel();

        bindYouTrackWorklogFieldNameProperties(viewModel);

        attachListeners(viewModel);
        initializeDefaultValues();
        bindInputElements(viewModel);
    }

    private void bindYouTrackWorklogFieldNameProperties(SettingsViewModel viewModel) {
        youtrackWorklogFielNameLabel.visibleProperty().bind(viewModel.youTrackVersionProperty().isEqualTo(SupportedVersions.getV2018_2()));
        youtrackWorklogFielNameField.visibleProperty().bind(viewModel.youTrackVersionProperty().isEqualTo(SupportedVersions.getV2018_2()));
        youtrackWorklogFielNameHelpLink.visibleProperty().bind(viewModel.youTrackVersionProperty().isEqualTo(SupportedVersions.getV2018_2()));
        youtrackWorklogFielNameHint.visibleProperty().bind(viewModel.youTrackVersionProperty().isEqualTo(SupportedVersions.getV2018_2()));

        youtrackWorklogFielNameField.textProperty().bindBidirectional(viewModel.youTrackWorkdateFieldNameProperty());
        youtrackWorklogFielNameHelpLink.setOnAction(event -> {
            String helpUrl = "https://github.com/pbauerochse/youtrack-worklog-viewer/wiki/Work-Date-Field-Help";
            LOGGER.debug("Opening page {} in browser", helpUrl);
            Platform.runLater(() -> WorklogViewer.getInstance().getHostServices().showDocument(helpUrl));
        });
    }

    private void initializeDefaultValues() {
        IntStream.range(1, 25).forEach(workhoursComboBox.getItems()::add);

        // Version Combobox Values
        youtrackVersionField.getItems().addAll(YouTrackConnectorLocator.getSupportedVersions());
        youtrackVersionField.setConverter(new YouTrackVersionStringConverter());

        // Theme Combobox
        themeComboBox.getItems().addAll(Theme.values());
    }

    private void bindInputElements(SettingsViewModel viewModel) {
        youtrackUrlField.textProperty().bindBidirectional(viewModel.youTrackUrlProperty());
        youtrackVersionField.valueProperty().bindBidirectional(viewModel.youTrackVersionProperty());
        youtrackUsernameField.textProperty().bindBidirectional(viewModel.youTrackUsernameProperty());
        youtrackPermanentTokenField.textProperty().bindBidirectional(viewModel.youTrackPermanentTokenProperty());

        themeComboBox.valueProperty().bindBidirectional(viewModel.themeProperty());
        workhoursComboBox.valueProperty().bindBidirectional(viewModel.workhoursProperty().asObject());
        showAllWorklogsCheckBox.selectedProperty().bindBidirectional(viewModel.showAllWorklogsProperty());
        showStatisticsCheckBox.selectedProperty().bindBidirectional(viewModel.showStatisticsProperty());
        loadDataAtStartupCheckBox.selectedProperty().bindBidirectional(viewModel.loadDataAtStartupProperty());
        showDecimalsInExcel.selectedProperty().bindBidirectional(viewModel.showDecimalsInExcelProperty());

        mondayCollapseCheckbox.selectedProperty().bindBidirectional(viewModel.collapseStateMondayProperty());
        tuesdayCollapseCheckbox.selectedProperty().bindBidirectional(viewModel.collapseStateTuesdayProperty());
        wednesdayCollapseCheckbox.selectedProperty().bindBidirectional(viewModel.collapseStateWednesdayProperty());
        thursdayCollapseCheckbox.selectedProperty().bindBidirectional(viewModel.collapseStateThursdayProperty());
        fridayCollapseCheckbox.selectedProperty().bindBidirectional(viewModel.collapseStateFridayProperty());
        saturdayCollapseCheckbox.selectedProperty().bindBidirectional(viewModel.collapseStateSaturdayProperty());
        sundayCollapseCheckbox.selectedProperty().bindBidirectional(viewModel.collapseStateSundayProperty());

        mondayHighlightCheckbox.selectedProperty().bindBidirectional(viewModel.highlightStateMondayProperty());
        tuesdayHighlightCheckbox.selectedProperty().bindBidirectional(viewModel.highlightStateTuesdayProperty());
        wednesdayHighlightCheckbox.selectedProperty().bindBidirectional(viewModel.highlightStateWednesdayProperty());
        thursdayHighlightCheckbox.selectedProperty().bindBidirectional(viewModel.highlightStateThursdayProperty());
        fridayHighlightCheckbox.selectedProperty().bindBidirectional(viewModel.highlightStateFridayProperty());
        saturdayHighlightCheckbox.selectedProperty().bindBidirectional(viewModel.highlightStateSaturdayProperty());
        sundayHighlightCheckbox.selectedProperty().bindBidirectional(viewModel.highlightStateSundayProperty());
    }

    private void attachListeners(SettingsViewModel viewModel) {
        SimpleBooleanProperty hadMissingSettingsWhenOpened = new SimpleBooleanProperty(viewModel.getHasMissingConnectionSettings());

        // enable cancel button only when settings are valid or have been valid
        // when the form was shown, to allow the user cancel invalid inputs
        cancelSettingsButton.disableProperty().bind(hadMissingSettingsWhenOpened.and(viewModel.hasMissingConnectionSettingsProperty()));

        cancelSettingsButton.setOnAction(event -> {
            LOGGER.debug("Cancel clicked");
            viewModel.discardChanges();
            closeSettingsDialogue();
        });

        saveSettingsButton.disableProperty().bind(viewModel.hasMissingConnectionSettingsProperty());
        saveSettingsButton.setOnAction(event -> {
            LOGGER.debug("Save settings clicked");
            viewModel.saveChanges();
            closeSettingsDialogue();
        });
    }

    @FXML
    public void showSettingsHelp() {
        String helpUrl = resourceBundle.getString("view.settings.authentication.help_url");
        LOGGER.debug("Opening page {} in browser", helpUrl);
        Platform.runLater(() -> WorklogViewer.getInstance().getHostServices().showDocument(helpUrl));
    }

    private void closeSettingsDialogue() {
        LOGGER.debug("Closing settings dialogue");
        Window window = saveSettingsButton.getScene().getWindow();
        window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

}
