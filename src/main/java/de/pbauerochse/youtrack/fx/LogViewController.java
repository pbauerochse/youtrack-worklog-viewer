package de.pbauerochse.youtrack.fx;

import ch.qos.logback.classic.spi.ILoggingEvent;
import de.pbauerochse.youtrack.logging.ListenableLimitedLogMessagesAppender;
import de.pbauerochse.youtrack.logging.LogMessageListener;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Patrick Bauerochse
 * @since 01.07.15
 */
public class LogViewController implements Initializable, LogMessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogViewController.class);

    @FXML
    private TextArea logMessageTextArea;

    private EventHandler<WindowEvent> MODAL_CLOSE_EVENT_LISTENER = event -> {
        ListenableLimitedLogMessagesAppender logMessagesAppender = ListenableLimitedLogMessagesAppender.getInstance();
        if (logMessagesAppender != null) {
            logMessagesAppender.removeListener(this);
        }
    };

    private ChangeListener<Window> WINDOW_CHANGE_LISTENER = (observable, oldValue, newValue) -> {
        if (oldValue != null) {
            oldValue.removeEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, MODAL_CLOSE_EVENT_LISTENER);
        }

        if (newValue != null) {
            newValue.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, MODAL_CLOSE_EVENT_LISTENER);
        }
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.debug("Initializing");

        logMessageTextArea.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                LOGGER.debug("Removing WindowChangeListener");
                oldValue.windowProperty().removeListener(WINDOW_CHANGE_LISTENER);
            }

            if (newValue != null) {
                LOGGER.debug("Adding WindowChangeListener");
                newValue.windowProperty().addListener(WINDOW_CHANGE_LISTENER);
            }
        });

        ListenableLimitedLogMessagesAppender logMessagesAppender = ListenableLimitedLogMessagesAppender.getInstance();
        if (logMessagesAppender != null) {
            logMessagesAppender.addListener(this);
            logMessageTextArea.setText(logMessagesAppender.getLogMessages());
        }
    }

    @Override
    public void onLogMessage(String formattedLogMessage, ILoggingEvent originalEvent) {
        // important: do not log anything in here otherwise you will get an infinite logging loop
        Platform.runLater(() -> logMessageTextArea.appendText(formattedLogMessage));
    }
}
