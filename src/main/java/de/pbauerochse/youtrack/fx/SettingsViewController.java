package de.pbauerochse.youtrack.fx;

import de.pbauerochse.youtrack.util.SettingsUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Patrick Bauerochse
 * @since 15.04.15
 */
public class SettingsViewController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsViewController.class);

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
    private CheckBox autoloadLastTimerangeCheckBox;

    @FXML
    private Button saveSettingsButton;

    @FXML
    private Button cancelSettingsButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.debug("Initializing");
        SettingsUtil.Settings settings = SettingsUtil.loadSettings();

        for (int i = 1; i <= 24; i++) {
            workhoursComboBox.getItems().add(i);
        }

        // button clicks
        cancelSettingsButton.setOnAction(event -> ((Node) event.getSource()).getScene().getWindow().hide());
        saveSettingsButton.setOnAction(event -> {
            LOGGER.debug("Save settings clicked");
            settings.setYoutrackUrl(youtrackUrlField.getText());
            settings.setYoutrackUsername(youtrackUsernameField.getText());
            settings.setYoutrackPassword(youtrackPasswordField.getText());
            settings.setWorkHoursADay(workhoursComboBox.getSelectionModel().getSelectedItem());
            settings.setShowAllWorklogs(showAllWorklogsCheckBox.isSelected());
            settings.setShowStatistics(showStatisticsCheckBox.isSelected());
            settings.setLoadDataAtStartup(autoloadLastTimerangeCheckBox.isSelected());

            ((Node) event.getSource()).getScene().getWindow().hide();
        });

        // prepopulate fields from settings
        youtrackUrlField.setText(settings.getYoutrackUrl());
        youtrackUsernameField.setText(settings.getYoutrackUsername());
        youtrackPasswordField.setText(settings.getYoutrackPassword());
        workhoursComboBox.getSelectionModel().select((Integer) settings.getWorkHoursADay());
        showAllWorklogsCheckBox.setSelected(settings.isShowAllWorklogs());
        showStatisticsCheckBox.setSelected(settings.isShowStatistics());
        autoloadLastTimerangeCheckBox.setSelected(settings.isLoadDataAtStartup());
    }
}
