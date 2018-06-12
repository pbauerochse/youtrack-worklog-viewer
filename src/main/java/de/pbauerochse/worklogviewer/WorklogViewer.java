package de.pbauerochse.worklogviewer;

import de.pbauerochse.worklogviewer.fx.MainViewController;
import de.pbauerochse.worklogviewer.fx.Theme;
import de.pbauerochse.worklogviewer.settings.Settings;
import de.pbauerochse.worklogviewer.settings.SettingsUtil;
import de.pbauerochse.worklogviewer.settings.SettingsViewModel;
import de.pbauerochse.worklogviewer.util.ExceptionUtil;
import de.pbauerochse.worklogviewer.util.FormattingUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Main class to start the Java FX UI
 */
public class WorklogViewer extends Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorklogViewer.class);

    private static WorklogViewer instance;

    public static WorklogViewer getInstance() {
        if (instance == null) {
            throw ExceptionUtil.getIllegalStateException("exceptions.main.instance");
        }
        return instance;
    }

    public static void main(String[] args) {
        launch(args);
    }

    private Stage primaryStage;

    @Override
    public void stop() {
        SettingsUtil.saveSettings();
        MainViewController.EXECUTOR.shutdownNow();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        instance = this;
        this.primaryStage = primaryStage;

        Settings settings = SettingsUtil.getSettings();
        SettingsViewModel settingsViewModel = SettingsUtil.getSettingsViewModel();

        LOGGER.info("Default Locale: {}", Locale.getDefault());
        LOGGER.info("Default Charset: {}", Charset.defaultCharset());
        LOGGER.info("Default TimeZone: {}", TimeZone.getDefault().toZoneId());
        LOGGER.info("Theme: {}", settings.getTheme());

        FXMLLoader loader = new FXMLLoader(Charset.forName("utf-8"));
        loader.setResources(FormattingUtil.RESOURCE_BUNDLE);

        Parent root = loader.load(WorklogViewer.class.getResource("/fx/views/main.fxml"), FormattingUtil.RESOURCE_BUNDLE);
        Scene mainScene = new Scene(root, settings.getWindowSettings().getWidth(), settings.getWindowSettings().getHeight());

        mainScene.getStylesheets().add("/fx/css/base-styling.css");
        mainScene.getStylesheets().add(settingsViewModel.getTheme().getStylesheet());

        settingsViewModel.themeProperty().addListener((observable, oldValue, newValue) -> {
            // remove unused stylesheet files
            Arrays.stream(Theme.values())
                    .filter(it -> it != newValue)
                    .map(Theme::getStylesheet)
                    .forEach(mainScene.getStylesheets()::remove);

            // add new theme
            mainScene.getStylesheets().add(newValue.getStylesheet());
        });


        primaryStage.setTitle("YouTrack Worklog Viewer " + FormattingUtil.getFormatted("release.version"));
        primaryStage.setScene(mainScene);
        primaryStage.setX(settings.getWindowSettings().getPositionX());
        primaryStage.setY(settings.getWindowSettings().getPositionY());
        primaryStage.show();

        primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> settings.getWindowSettings().setWidth(newValue.intValue()));
        primaryStage.heightProperty().addListener((observable, oldValue, newValue) -> settings.getWindowSettings().setHeight(newValue.intValue()));
        primaryStage.xProperty().addListener((observable, oldValue, newValue) -> settings.getWindowSettings().setPositionX(newValue.intValue()));
        primaryStage.yProperty().addListener((observable, oldValue, newValue) -> settings.getWindowSettings().setPositionY(newValue.intValue()));
    }

    public void requestShutdown() {
        LOGGER.debug("Shutdown requested");
        primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

}
