package de.pbauerochse.worklogviewer;

import javafx.application.Application;

/**
 * Main class must not extend Application due to
 * openjfx module restrictions in a fat jar.
 * <p>
 * see https://github.com/javafxports/openjdk-jfx/issues/236
 */
public class WorklogViewerLauncher {

    public static void main(String[] args) {
        Application.launch(WorklogViewer.class, args);
    }

}
