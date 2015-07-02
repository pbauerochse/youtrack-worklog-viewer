package de.pbauerochse.youtrack;

import de.pbauerochse.youtrack.util.ExceptionUtil;
import de.pbauerochse.youtrack.util.FormattingUtil;
import de.pbauerochse.youtrack.util.SettingsUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

/**
 * @author Patrick Bauerochse
 * @since 01.04.15
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
    public void stop() throws Exception {
        SettingsUtil.saveSettings();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        instance = this;
        this.primaryStage = primaryStage;

        SettingsUtil.Settings settings = SettingsUtil.loadSettings();

        FXMLLoader loader = new FXMLLoader(Charset.forName("utf-8"));
        loader.setResources(FormattingUtil.RESOURCE_BUNDLE);

        Parent root = loader.load(WorklogViewer.class.getResource("/fx/views/main.fxml"), FormattingUtil.RESOURCE_BUNDLE);
        Scene mainScene = new Scene(root, settings.getWindowWidth(), settings.getWindowHeight());
        mainScene.getStylesheets().add("/fx/css/main.css");

        primaryStage.setTitle("YouTrack Worklog Viewer");
        primaryStage.setScene(mainScene);
        primaryStage.setX(settings.getWindowX());
        primaryStage.setY(settings.getWindowY());
        primaryStage.show();

        primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> settings.setWindowWidth(newValue.intValue()));
        primaryStage.heightProperty().addListener((observable, oldValue, newValue) -> settings.setWindowHeight(newValue.intValue()));
        mainScene.getWindow().xProperty().addListener((observable, oldValue, newValue) -> settings.setWindowX(newValue.intValue()));
        mainScene.getWindow().yProperty().addListener((observable, oldValue, newValue) -> settings.setWindowY(newValue.intValue()));
    }

    public void requestShutdown() {
        LOGGER.debug("Shutdown requested");
        primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

}
