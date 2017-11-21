package de.pbauerochse.worklogviewer.fx;

import de.pbauerochse.worklogviewer.WorklogViewer;
import de.pbauerochse.worklogviewer.fx.converter.YouTrackAuthenticationMethodStringConverter;
import de.pbauerochse.worklogviewer.fx.converter.YouTrackVersionStringConverter;
import de.pbauerochse.worklogviewer.util.SettingsUtil;
import de.pbauerochse.worklogviewer.youtrack.YouTrackAuthenticationMethod;
import de.pbauerochse.worklogviewer.youtrack.YouTrackVersion;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

import static java.time.DayOfWeek.*;
import static javafx.beans.binding.Bindings.or;

/**
 * @author Patrick Bauerochse
 * @since 15.04.15
 */
public class SettingsViewController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsViewController.class);

    private static final String MYJETBRAINS_HOST = "myjetbrains.com";
    private static final String MYJETBRAINS_HOSTED_YOUTRACK_PATH = "/youtrack";

    @FXML
    private Text youtrackVersionRequiredLabel;

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
        SettingsUtil.Settings settings = SettingsUtil.loadSettings();

        for (int i = 1; i <= 24; i++) {
            workhoursComboBox.getItems().add(i);
        }

        youtrackVersionField.getItems().addAll(YouTrackVersion.values());
        youtrackVersionField.setConverter(new YouTrackVersionStringConverter());

        // only show version required label when no version set yet
        youtrackVersionRequiredLabel.visibleProperty().bind(youtrackVersionField.getSelectionModel().selectedItemProperty().isNull());
        youtrackVersionRequiredLabel.managedProperty().bind(youtrackVersionField.getSelectionModel().selectedItemProperty().isNull());

        youtrackAuthenticationMethodField.getItems().addAll(YouTrackAuthenticationMethod.values());
        youtrackAuthenticationMethodField.setConverter(new YouTrackAuthenticationMethodStringConverter());

        youtrackUrlField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                youtrackOAuthHubUrlField.setText(getHubUrl(newValue).toExternalForm());
            } catch (MalformedURLException e) {}
        });

        // disable oauth fields when other authentication method is selected
        youtrackOAuthHubUrlField.disableProperty().bind(youtrackAuthenticationMethodField.getSelectionModel().selectedItemProperty().isNotEqualTo(YouTrackAuthenticationMethod.OAUTH2));
        youtrackOAuthServiceIdField.disableProperty().bind(youtrackAuthenticationMethodField.getSelectionModel().selectedItemProperty().isNotEqualTo(YouTrackAuthenticationMethod.OAUTH2));
        youtrackOAuthServiceSecretField.disableProperty().bind(youtrackAuthenticationMethodField.getSelectionModel().selectedItemProperty().isNotEqualTo(YouTrackAuthenticationMethod.OAUTH2));

        // disabled token auth field when other authentication method is selected
        youtrackPermanentTokenField.disableProperty().bind(youtrackAuthenticationMethodField.getSelectionModel().selectedItemProperty().isNotEqualTo(YouTrackAuthenticationMethod.PERMANENT_TOKEN));

        // disable username and password field when bearer is used
        youtrackUsernameField.disableProperty().bind(youtrackAuthenticationMethodField.getSelectionModel().selectedItemProperty().isEqualTo(YouTrackAuthenticationMethod.PERMANENT_TOKEN));
        youtrackPasswordField.disableProperty().bind(youtrackAuthenticationMethodField.getSelectionModel().selectedItemProperty().isEqualTo(YouTrackAuthenticationMethod.PERMANENT_TOKEN));

        updateComponentsFromSettings(settings);

        // cancel button disabled, when crucial properties not set
        BooleanBinding cancelDisabledProperty = or(
                or(youtrackUrlField.textProperty().isEmpty(), youtrackAuthenticationMethodField.getSelectionModel().selectedItemProperty().isNull()),
                youtrackVersionField.getSelectionModel().selectedItemProperty().isNull()
        );

        cancelSettingsButton.disableProperty().bind(cancelDisabledProperty);
        cancelSettingsButton.setOnAction(event -> {
            updateComponentsFromSettings(settings);
            closeSettingsDialogue();
        });

        saveSettingsButton.setOnAction(event -> {
            LOGGER.debug("Save settings clicked");
            applyToSettings(settings);
            closeSettingsDialogue();
        });
    }

    private void updateComponentsFromSettings(SettingsUtil.Settings settings) {
        youtrackUrlField.setText(settings.getYoutrackUrl());

        if (settings.getYouTrackVersion() != null) {
            youtrackVersionField.getSelectionModel().select(settings.getYouTrackVersion());
        }

        if (settings.getYouTrackAuthenticationMethod() != null) {
            youtrackAuthenticationMethodField.getSelectionModel().select(settings.getYouTrackAuthenticationMethod());
        }

        youtrackUsernameField.setText(settings.getYoutrackUsername());
        youtrackPasswordField.setText(settings.getYoutrackPassword());

        youtrackOAuthHubUrlField.setText(settings.getYoutrackOAuthHubUrl());
        youtrackOAuthServiceIdField.setText(settings.getYoutrackOAuthServiceId());
        youtrackOAuthServiceSecretField.setText(settings.getYoutrackOAuthServiceSecret());

        youtrackPermanentTokenField.setText(settings.getYoutrackPermanentToken());

        workhoursComboBox.getSelectionModel().select((Integer) settings.getWorkHoursADay());
        showAllWorklogsCheckBox.setSelected(settings.isShowAllWorklogs());
        showStatisticsCheckBox.setSelected(settings.isShowStatistics());
        loadDataAtStartupCheckBox.setSelected(settings.isLoadDataAtStartup());
        showDecimalsInExcel.setSelected(settings.isShowDecimalHourTimesInExcelReport());

        mondayCollapseCheckbox.setSelected(settings.hasCollapseState(MONDAY));
        mondayHighlightCheckbox.setSelected(settings.hasHighlightState(MONDAY));

        tuesdayCollapseCheckbox.setSelected(settings.hasCollapseState(TUESDAY));
        tuesdayHighlightCheckbox.setSelected(settings.hasHighlightState(TUESDAY));

        wednesdayCollapseCheckbox.setSelected(settings.hasCollapseState(WEDNESDAY));
        wednesdayHighlightCheckbox.setSelected(settings.hasHighlightState(WEDNESDAY));

        thursdayCollapseCheckbox.setSelected(settings.hasCollapseState(THURSDAY));
        thursdayHighlightCheckbox.setSelected(settings.hasHighlightState(THURSDAY));

        fridayCollapseCheckbox.setSelected(settings.hasCollapseState(FRIDAY));
        fridayHighlightCheckbox.setSelected(settings.hasHighlightState(FRIDAY));

        saturdayCollapseCheckbox.setSelected(settings.hasCollapseState(SATURDAY));
        saturdayHighlightCheckbox.setSelected(settings.hasHighlightState(SATURDAY));

        sundayCollapseCheckbox.setSelected(settings.hasCollapseState(SUNDAY));
        sundayHighlightCheckbox.setSelected(settings.hasHighlightState(SUNDAY));
    }

    private void applyToSettings(SettingsUtil.Settings settings) {
        settings.setYoutrackUrl(youtrackUrlField.getText());
        settings.setYouTrackAuthenticationMethod(youtrackAuthenticationMethodField.getSelectionModel().getSelectedItem());
        settings.setYoutrackUsername(youtrackUsernameField.getText());
        settings.setYoutrackPassword(youtrackPasswordField.getText());
        settings.setYoutrackOAuthHubUrl(youtrackOAuthHubUrlField.getText());
        settings.setYoutrackOAuthServiceId(youtrackOAuthServiceIdField.getText());
        settings.setYoutrackOAuthServiceSecret(youtrackOAuthServiceSecretField.getText());
        settings.setYoutrackPermanentToken(youtrackPermanentTokenField.getText());
        settings.setYouTrackVersion(youtrackVersionField.getSelectionModel().getSelectedItem());

        settings.setWorkHoursADay(workhoursComboBox.getSelectionModel().getSelectedItem());
        settings.setShowAllWorklogs(showAllWorklogsCheckBox.isSelected());
        settings.setShowStatistics(showStatisticsCheckBox.isSelected());
        settings.setLoadDataAtStartup(loadDataAtStartupCheckBox.isSelected());
        settings.setShowDecimalHourTimesInExcelReport(showDecimalsInExcel.isSelected());

        settings.setCollapseState(MONDAY, mondayCollapseCheckbox.isSelected());
        settings.setCollapseState(TUESDAY, tuesdayCollapseCheckbox.isSelected());
        settings.setCollapseState(WEDNESDAY, wednesdayCollapseCheckbox.isSelected());
        settings.setCollapseState(THURSDAY, thursdayCollapseCheckbox.isSelected());
        settings.setCollapseState(FRIDAY, fridayCollapseCheckbox.isSelected());
        settings.setCollapseState(SATURDAY, saturdayCollapseCheckbox.isSelected());
        settings.setCollapseState(SUNDAY, sundayCollapseCheckbox.isSelected());


        settings.setHighlightState(MONDAY, mondayHighlightCheckbox.isSelected());
        settings.setHighlightState(TUESDAY, tuesdayHighlightCheckbox.isSelected());
        settings.setHighlightState(WEDNESDAY, wednesdayHighlightCheckbox.isSelected());
        settings.setHighlightState(THURSDAY, thursdayHighlightCheckbox.isSelected());
        settings.setHighlightState(FRIDAY, fridayHighlightCheckbox.isSelected());
        settings.setHighlightState(SATURDAY, saturdayHighlightCheckbox.isSelected());
        settings.setHighlightState(SUNDAY, sundayHighlightCheckbox.isSelected());
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

    private static URL getHubUrl(String baseUrl) throws MalformedURLException {
        if (StringUtils.isBlank(baseUrl)) {
            return new URL("");
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

        return new URL(sb.toString());
    }
}
