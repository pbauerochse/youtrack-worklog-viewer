package de.pbauerochse.worklogviewer.fx

import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted

/**
 * Available themes for the WorklogViewer UI
 */
enum class Theme(val stylesheet: String) {

    /**
     * The light theme as it has always been
     * present in the worklog viewer
     */
    LIGHT("/fx/css/light.css"),

    /**
     * A new darker theme
     */
    DARK("/fx/css/dark.css");

    override fun toString(): String {
        return getFormatted("theme.${name.toLowerCase()}.label")
    }
}
