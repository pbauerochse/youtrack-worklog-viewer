package de.pbauerochse.worklogviewer.fx;

/**
 * Available themes for the WorklogViewer UI
 */
public enum Theme {

    /**
     * The light theme as it has always been
     * present in the worklog viewer
     */
    LIGHT("/fx/css/light.css"),

    /**
     * A new darker theme
     */
    DARK("/fx/css/dark.css");

    private final String stylesheet;

    Theme(String stylesheet) {
        this.stylesheet = stylesheet;
    }

    public String getStylesheet() {
        return stylesheet;
    }
}
