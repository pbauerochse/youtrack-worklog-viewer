package de.pbauerochse.worklogviewer.fx;

import de.pbauerochse.worklogviewer.logging.LogMessageListener;
import de.pbauerochse.worklogviewer.logging.WorklogViewerLogs;
import de.pbauerochse.worklogviewer.settings.WorklogViewerFiles;
import de.pbauerochse.worklogviewer.util.ExceptionUtil;
import de.pbauerochse.worklogviewer.util.FormattingUtil;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for the LogMessages view. Stores the retrieved Log Messages
 * and displays them in an appropriate FX component.
 * <p>
 * The component only gets updated when visible
 */
public class LogViewController implements Initializable, LogMessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogViewController.class);

    @FXML
    private ListView<String> logMessagesComponent;

    private final EventHandler<WindowEvent> modalCloseEventListener = event -> WorklogViewerLogs.removeListener(this);
    private final ChangeListener<Window> windowChangeListener = (observable, oldValue, newValue) -> {
        if (oldValue != null) {
            oldValue.removeEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, modalCloseEventListener);
        }

        if (newValue != null) {
            newValue.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, modalCloseEventListener);
        }
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.debug("Initializing");
        logMessagesComponent.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                LOGGER.debug("Removing WindowChangeListener");
                oldValue.windowProperty().removeListener(windowChangeListener);
            }

            if (newValue != null) {
                LOGGER.debug("Adding WindowChangeListener");
                newValue.windowProperty().addListener(windowChangeListener);
            }
        });

        WorklogViewerLogs.addListener(this);
        List<String> pendingLogMessages = WorklogViewerLogs.getRecentLogMessages();
        LOGGER.debug("Adding {} Log messages to component", pendingLogMessages.size());
        addLogMessages(pendingLogMessages);
    }

    @Override
    public void onLogMessage(@NotNull List<String> messages) {
        // important: do not log anything in here otherwise you will get an infinite logging loop
        addLogMessages(messages);
    }

    public void showSaveLogFileDialog() {
        Platform.runLater(() -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName("worklog-viewer.log");
            fileChooser.setTitle(FormattingUtil.getFormatted("view.logs.savefile"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Worklog Viewer Log File", "*.log"));
            File file = fileChooser.showSaveDialog(logMessagesComponent.getScene().getWindow());

            if (file != null) {
                copyLogFileTo(file);
            }
        });
    }

    private void copyLogFileTo(@NotNull File targetFile) {
        try {
            Files.copy(WorklogViewerFiles.LOG_FILE.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw ExceptionUtil.getIllegalArgumentException("exceptions.logs.savefile", e, targetFile.getAbsolutePath());
        }
    }

    private void addLogMessages(@NotNull List<String> messages) {
        Platform.runLater(() -> {
            logMessagesComponent.getItems().addAll(messages);
            logMessagesComponent.scrollTo(logMessagesComponent.getItems().size() - 1);
        });
    }
}
