package de.pbauerochse.worklogviewer.fx;

import de.pbauerochse.worklogviewer.WorklogViewer;
import de.pbauerochse.worklogviewer.util.FormattingUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Patrick Bauerochse
 * @since 06.07.15
 */
public class AboutController implements Initializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AboutController.class);

    @FXML
    private VBox worklogViewerDescriptionContainer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Label description = new Label(FormattingUtil.getFormatted("release.description"));
        description.setWrapText(true);
        description.setTextAlignment(TextAlignment.JUSTIFY);
        VBox.setVgrow(description, Priority.ALWAYS);
        VBox.setMargin(description, new Insets(0, 0, 20, 0));

        worklogViewerDescriptionContainer.getChildren().addAll(
                description,
                getLink("YouTrack by JetBrains", "https://www.jetbrains.com/youtrack/"),
                getLink("YouTrack Worklog Viewer @github.com", "https://github.com/pbauerochse/youtrack-worklog-viewer"),
                getLink(FormattingUtil.getFormatted("release.license"), "https://github.com/pbauerochse/youtrack-worklog-viewer/blob/master/LICENSE.txt"),
                getLink(FormattingUtil.getFormatted("release.icons"), "http://www.famfamfam.com/lab/icons/silk/")
        );
    }

    private Hyperlink getLink(String title, String target) {
        Hyperlink hyperlink = new Hyperlink(title);
        hyperlink.setOnAction(event -> Platform.runLater(() -> WorklogViewer.getInstance().getHostServices().showDocument(target)));
        return hyperlink;
    }
}
