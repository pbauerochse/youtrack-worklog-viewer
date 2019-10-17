package de.pbauerochse.worklogviewer.fx

import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted

/**
 * Available themes for the WorklogViewer UI
 */
enum class Theme(val stylesheet: String, val webviewStylesheet : String) {

    /**
     * The light theme as it has always been
     * present in the worklog viewer
     */
    LIGHT("/fx/css/light.css", "/fx/css/webview.light.css"),

    /**
     * A new darker theme
     */
    DARK("/fx/css/dark.css", "/fx/css/webview.dark.css");

    override fun toString(): String {
        return getFormatted("theme.${name.toLowerCase()}.label")
    }
}
