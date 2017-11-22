package de.pbauerochse.worklogviewer.fx;

import de.pbauerochse.worklogviewer.WorklogViewer;
import de.pbauerochse.worklogviewer.fx.converter.YouTrackAuthenticationMethodStringConverter;
import de.pbauerochse.worklogviewer.fx.converter.YouTrackVersionStringConverter;
import de.pbauerochse.worklogviewer.settings.SettingsUtil;
import de.pbauerochse.worklogviewer.settings.SettingsViewModel;
import de.pbauerochse.worklogviewer.youtrack.YouTrackAuthenticationMethod;
import de.pbauerochse.worklogviewer.youtrack.YouTrackService;
import de.pbauerochse.worklogviewer.youtrack.YouTrackServiceFactory;
import de.pbauerochse.worklogviewer.youtrack.YouTrackVersion;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.IntStream;

/**
 * @author Patrick Bauerochse
 * @since 15.04.15
 */
public class SettingsViewController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsViewController.class);

    private static final String MYJETBRAINS_HOST = "myjetbrains.com";
    private static final String MYJETBRAINS_HOSTED_YOUTRACK_PATH = "/youtrack";

    @FXML
    private TextField youtrackUrlField;

    @FXML
    private ComboBox<YouTrackVersion> youtrackVersionField;

    @FXML
    private TextField youtrackUsernameField;

    @FXML
    private PasswordField youtrackPasswordField;

    @FXML
    private ComboBox<Integer> workhoursComboBox;

    @FXML
    private CheckBox showAllWorklogsCheckBox;

    @FXML
    private CheckBox showStatisticsCheckBox;

    @FXML
    private CheckBox loadDataAtStartupCheckBox;

    @FXML
    private CheckBox showDecimalsInExcel;

    @FXML
    private ComboBox<YouTrackAuthenticationMethod> youtrackAuthenticationMethodField;

    @FXML
    private TextField youtrackOAuthHubUrlField;

    @FXML
    private TextField youtrackOAuthServiceIdField;

    @FXML
    private PasswordField youtrackOAuthServiceSecretField;

    @FXML
    private PasswordField youtrackPermanentTokenField;

    @FXML
    private Button saveSettingsButton;

    @FXML
    private Button cancelSettingsButton;

    @FXML
    private CheckBox mondayCollapseCheckbox;

    @FXML
    private CheckBox mondayHighlightCheckbox;

    @FXML
    private CheckBox tuesdayCollapseCheckbox;

    @FXML
    private CheckBox tuesdayHighlightCheckbox;

    @FXML
    private CheckBox wednesdayCollapseCheckbox;

    @FXML
    private CheckBox wednesdayHighlightCheckbox;

    @FXML
    private CheckBox thursdayCollapseCheckbox;

    @FXML
    private CheckBox thursdayHighlightCheckbox;

    @FXML
    private CheckBox fridayCollapseCheckbox;

    @FXML
    private CheckBox fridayHighlightCheckbox;

    @FXML
    private CheckBox saturdayCollapseCheckbox;

    @FXML
    private CheckBox saturdayHighlightCheckbox;

    @FXML
    private CheckBox sundayCollapseCheckbox;

    @FXML
    private CheckBox sundayHighlightCheckbox;

    private ResourceBundle resourceBundle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.debug("Initializing");
        this.resourceBundle = resources;

        SettingsViewModel viewModel = SettingsUtil.getSettingsViewModel();
        attachListeners(viewModel);
        initializeDefaultValues(viewModel);
        bindInputElements(viewModel);
    }

    private void initializeDefaultValues(SettingsViewModel viewModel) {
        IntStream.range(1, 25).forEach(workhoursComboBox.getItems()::add);

        // Version Combobox Values
        youtrackVersionField.getItems().addAll(YouTrackVersion.values());
        youtrackVersionField.setConverter(new YouTrackVersionStringConverter());

        // Authentication Methods Values
        if (viewModel.getYouTrackVersion() != null) {
            youtrackAuthenticationMethodField.getItems().addAll(YouTrackAuthenticationMethod.values());
        }
        youtrackAuthenticationMethodField.setConverter(new YouTrackAuthenticationMethodStringConverter());
    }

    private void bindInputElements(SettingsViewModel viewModel) {
        youtrackUrlField.textProperty().bindBidirectional(viewModel.youTrackUrlProperty());
        youtrackVersionField.valueProperty().bindBidirectional(viewModel.youTrackVersionProperty());
        youtrackAuthenticationMethodField.valueProperty().bindBidirectional(viewModel.youTrackAuthenticationMethodProperty());
        youtrackUsernameField.textProperty().bindBidirectional(viewModel.youTrackUsernameProperty());
        youtrackPasswordField.textProperty().bindBidirectional(viewModel.youTrackPasswordProperty());
        youtrackOAuthHubUrlField.textProperty().bindBidirectional(viewModel.youTrackHubUrlProperty());
        youtrackOAuthServiceIdField.textProperty().bindBidirectional(viewModel.youTrackOAuth2ServiceIdProperty());
        youtrackOAuthServiceSecretField.textProperty().bindBidirectional(viewModel.youTrackOAuth2ServiceSecretProperty());
        youtrackPermanentTokenField.textProperty().bindBidirectional(viewModel.youTrackPermanentTokenProperty());

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
        // update hub url from youtrack base url
        youtrackUrlField.textProperty().addListener((observable, oldValue, newValue) -> youtrackOAuthHubUrlField.setText(getHubUrl(newValue)));

        // only show authentication methods, that are supported by the selected version
        youtrackVersionField.getSelectionModel().selectedItemProperty().addListener((observable, oldVersion, newVersion) -> {
            YouTrackService service = YouTrackServiceFactory.getYouTrackService(newVersion);
            List<YouTrackAuthenticationMethod> validAuthenticationMethods = service.getValidAuthenticationMethods();

            YouTrackAuthenticationMethod currentlySelectedAuthenticationMethod = youtrackAuthenticationMethodField.getValue();
            youtrackAuthenticationMethodField.setItems(FXCollections.observableArrayList(validAuthenticationMethods));

            if (!validAuthenticationMethods.contains(currentlySelectedAuthenticationMethod)) {
                // select the first in the list
                youtrackAuthenticationMethodField.setValue(validAuthenticationMethods.get(0));
            }
        });

        // Hide username password field, when they are not needed
        youtrackUsernameField.visibleProperty().bind(viewModel.requiresUsernamePasswordProperty());
        youtrackUsernameField.managedProperty().bind(viewModel.requiresUsernamePasswordProperty());

        youtrackPasswordField.visibleProperty().bind(viewModel.requiresUsernamePasswordProperty());
        youtrackPasswordField.managedProperty().bind(viewModel.requiresUsernamePasswordProperty());

        // Hide OAuth2 fields when they are not needed
        youtrackOAuthServiceSecretField.visibleProperty().bind(viewModel.requiresOAuthSettingsProperty());
        youtrackOAuthServiceSecretField.managedProperty().bind(viewModel.requiresOAuthSettingsProperty());

        youtrackOAuthServiceIdField.visibleProperty().bind(viewModel.requiresOAuthSettingsProperty());
        youtrackOAuthServiceIdField.managedProperty().bind(viewModel.requiresOAuthSettingsProperty());

        youtrackOAuthHubUrlField.visibleProperty().bind(viewModel.requiresOAuthSettingsProperty());
        youtrackOAuthHubUrlField.managedProperty().bind(viewModel.requiresOAuthSettingsProperty());

        // Hide token field if not needed
        youtrackPermanentTokenField.visibleProperty().bind(viewModel.requiresPermanentTokenProperty());
        youtrackPermanentTokenField.managedProperty().bind(viewModel.requiresPermanentTokenProperty());

        cancelSettingsButton.setOnAction(event -> {
            LOGGER.debug("Cancel clicked");
            viewModel.discardChanges();
            closeSettingsDialogue();
        });

        viewModel.hasValidConnectionParametersProperty().addListener((observable, oldValue, newValue) -> LOGGER.debug("HasValidSettings Changed from {} to {}", oldValue, newValue));
        saveSettingsButton.disableProperty().bind(viewModel.hasValidConnectionParametersProperty().not());
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

    private static String getHubUrl(String baseUrl) {
        if (StringUtils.isBlank(baseUrl)) {
            return StringUtils.EMPTY;
        }

        StringBuilder sb = new StringBuilder(StringUtils.trim(baseUrl));

        while (sb.charAt(sb.length() - 1) == '/') {
            sb.deleteCharAt(sb.length() - 1);
        }

        sb.append("/hub");

        boolean seemsToBeJetbrainsHosted = StringUtils.containsIgnoreCase(baseUrl, MYJETBRAINS_HOST);
        int indexOfYoutrackPath = sb.indexOf(MYJETBRAINS_HOSTED_YOUTRACK_PATH);

        if (seemsToBeJetbrainsHosted && indexOfYoutrackPath >= 0) {
            sb.replace(indexOfYoutrackPath, indexOfYoutrackPath + MYJETBRAINS_HOSTED_YOUTRACK_PATH.length(), "");
        }

        try {
            return new URL(sb.toString()).toExternalForm();
        } catch (MalformedURLException e) {
            return StringUtils.EMPTY;
        }
    }
}
