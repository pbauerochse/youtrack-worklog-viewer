package de.pbauerochse.worklogviewer.util;

import de.pbauerochse.worklogviewer.WorklogViewer;
import javafx.application.Platform;
import javafx.scene.control.Hyperlink;

/**
 * Created by patrick on 01.11.15.
 */
public class HyperlinkUtil {

    public static Hyperlink createLink(String title, String target) {
        Hyperlink hyperlink = new Hyperlink(title);
        hyperlink.setOnAction(event -> Platform.runLater(() -> WorklogViewer.getInstance().getHostServices().showDocument(target)));
        return hyperlink;
    }

}
