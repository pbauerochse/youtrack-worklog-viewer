package de.pbauerochse.worklogviewer.fx;

import de.pbauerochse.worklogviewer.util.SettingsUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
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
    private CheckBox loadDataAtStartupCheckBox;

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
        workhoursComboBox.getSelectionModel().select((Integer) settings.getWorkHoursADay());

        youtrackUrlField.textProperty().bindBidirectional(settings.youtrackUrlProperty());
        youtrackUsernameField.textProperty().bindBidirectional(settings.youtrackUsernameProperty());
        youtrackPasswordField.textProperty().bindBidirectional(settings.youtrackPasswordProperty());
        showAllWorklogsCheckBox.selectedProperty().bindBidirectional(settings.showAllWorklogsProperty());
        showStatisticsCheckBox.selectedProperty().bindBidirectional(settings.showStatisticsProperty());
        loadDataAtStartupCheckBox.selectedProperty().bindBidirectional(settings.loadDataAtStartupProperty());

        settings.workHoursADayProperty().bind(workhoursComboBox.getSelectionModel().selectedItemProperty());

        // button clicks
        cancelSettingsButton.setOnAction(event -> closeSettingsDialogue());
        saveSettingsButton.setOnAction(event -> {
            LOGGER.debug("Save settings clicked");
            closeSettingsDialogue();
        });
    }

    private void closeSettingsDialogue() {
        LOGGER.debug("Closing settings dialogue");
        Window window = saveSettingsButton.getScene().getWindow();
        window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
    }
}
