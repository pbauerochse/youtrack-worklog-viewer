package de.pbauerochse.youtrack.fx;

import de.pbauerochse.youtrack.util.SettingsUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Patrick Bauerochse
 * @since 15.04.15
 */
public class SettingsViewController implements Initializable {

    @FXML
    private TextField youtrackUrlField;

    @FXML
    private TextField youtrackUsernameField;

    @FXML
    private PasswordField youtrackPasswordField;

    @FXML
    private ComboBox<Integer> workhoursComboBox;

    @FXML
    private Button saveSettingsButton;

    @FXML
    private Button cancelSettingsButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SettingsUtil.Settings settings = SettingsUtil.loadSettings();

        for (int i = 1; i <= 24; i++) {
            workhoursComboBox.getItems().add(i);
        }

        // button clicks
        cancelSettingsButton.setOnAction(event -> ((Node) event.getSource()).getScene().getWindow().hide());
        saveSettingsButton.setOnAction(event -> {
            settings.setYoutrackUrl(youtrackUrlField.getText());
            settings.setYoutrackUsername(youtrackUsernameField.getText());
            settings.setYoutrackPassword(youtrackPasswordField.getText());
            settings.setWorkHoursADay(workhoursComboBox.getSelectionModel().getSelectedItem());
            ((Node) event.getSource()).getScene().getWindow().hide();
        });

        // prepopulate fields from settings
        youtrackUrlField.setText(settings.getYoutrackUrl());
        youtrackUsernameField.setText(settings.getYoutrackUsername());
        youtrackPasswordField.setText(settings.getYoutrackPassword());
        workhoursComboBox.getSelectionModel().select((Integer) settings.getWorkHoursADay());
    }
}
