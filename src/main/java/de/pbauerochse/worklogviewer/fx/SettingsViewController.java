package de.pbauerochse.worklogviewer.fx;

import de.pbauerochse.worklogviewer.WorklogViewer;
import de.pbauerochse.worklogviewer.fx.converter.YouTrackAuthenticationMethodStringConverter;
import de.pbauerochse.worklogviewer.util.SettingsUtil;
import de.pbauerochse.worklogviewer.youtrack.connector.YouTrackAuthenticationMethod;
import javafx.application.Platform;
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
import java.util.ResourceBundle;

import static java.time.DayOfWeek.*;

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

        youtrackAuthenticationMethodField.getItems().addAll(YouTrackAuthenticationMethod.values());
        youtrackAuthenticationMethodField.setConverter(new YouTrackAuthenticationMethodStringConverter());

        youtrackUrlField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                youtrackOAuthHubUrlField.setText(getHubUrl(newValue).toExternalForm());
            } catch (MalformedURLException e) {}
        });

        // disable oauth fields when api is selected
        youtrackOAuthHubUrlField.disableProperty().bind(youtrackAuthenticationMethodField.getSelectionModel().selectedItemProperty().isEqualTo(YouTrackAuthenticationMethod.HTTP_API));
        youtrackOAuthServiceIdField.disableProperty().bind(youtrackAuthenticationMethodField.getSelectionModel().selectedItemProperty().isEqualTo(YouTrackAuthenticationMethod.HTTP_API));
        youtrackOAuthServiceSecretField.disableProperty().bind(youtrackAuthenticationMethodField.getSelectionModel().selectedItemProperty().isEqualTo(YouTrackAuthenticationMethod.HTTP_API));



        updateComponentsFromSettings(settings);

//        youtrackUrlField.textProperty().bindBidirectional(settings.youtrackUrlProperty());
//        youtrackUsernameField.textProperty().bindBidirectional(settings.youtrackUsernameProperty());
//        youtrackPasswordField.textProperty().bindBidirectional(settings.youtrackPasswordProperty());
//
//        youtrackOAuthHubUrlField.textProperty().bindBidirectional(settings.youtrackOAuthHubUrlProperty());
//        youtrackOAuthServiceIdField.textProperty().bindBidirectional(settings.youtrackOAuthServiceIdProperty());
//        youtrackOAuthServiceSecretField.textProperty().bindBidirectional(settings.youtrackOAuthServiceSecretProperty());
//
//        youtrackAuthenticationMethodField.getSelectionModel().select(settings.youTrackAuthenticationMethodProperty().get());
//        youtrackAuthenticationMethodField.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> settings.youTrackAuthenticationMethodProperty().set(newValue));
//
//        if (youtrackAuthenticationMethodField.getSelectionModel().getSelectedItem() == YouTrackAuthenticationMethod.OAUTH2 && StringUtils.isBlank(youtrackOAuthHubUrlField.getText())) {
//            try {
//                youtrackOAuthHubUrlField.setText(getHubUrl(youtrackUrlField.getText()).toExternalForm());
//            } catch (MalformedURLException e) {}
//        }
//
//        showAllWorklogsCheckBox.selectedProperty().bindBidirectional(settings.showAllWorklogsProperty());
//        showStatisticsCheckBox.selectedProperty().bindBidirectional(settings.showStatisticsProperty());
//        loadDataAtStartupCheckBox.selectedProperty().bindBidirectional(settings.loadDataAtStartupProperty());
//        showDecimalsInExcel.selectedProperty().bindBidirectional(settings.showDecimalHourTimesInExcelReportProperty());
//
//        settings.workHoursADayProperty().bind(workhoursComboBox.getSelectionModel().selectedItemProperty());

        // button clicks
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

        if (settings.getYouTrackAuthenticationMethod() != null) {
            youtrackAuthenticationMethodField.getSelectionModel().select(settings.getYouTrackAuthenticationMethod());
        }

        youtrackUsernameField.setText(settings.getYoutrackUsername());
        youtrackPasswordField.setText(settings.getYoutrackPassword());

        youtrackOAuthHubUrlField.setText(settings.getYoutrackOAuthHubUrl());
        youtrackOAuthServiceIdField.setText(settings.getYoutrackOAuthServiceId());
        youtrackOAuthServiceSecretField.setText(settings.getYoutrackOAuthServiceSecret());

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
