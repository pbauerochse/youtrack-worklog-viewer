package de.pbauerochse.worklogviewer;

/**
 * Main class must not extend Application due to
 * openjfx module restrictions in a fat jar.
 * <p>
 * see https://github.com/javafxports/openjdk-jfx/issues/236
 */
public class WorklogViewerLauncher {

    public static void main(String[] args) {
        WorklogViewer.main(args);
    }

}
