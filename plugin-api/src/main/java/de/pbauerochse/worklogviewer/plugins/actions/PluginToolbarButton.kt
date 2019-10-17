package de.pbauerochse.worklogviewer.plugins.actions

import java.io.InputStream

/**
 * Represents a button, that will be displayed in the main toolbar.
 * When the user clicks on that button, the actionHandler will be invoked
 */
interface PluginToolbarButton {

    /**
     * a small icon (e.g. 32x32 pixels) that will
     * be rendered onto the toolbar button
     */
    val icon : InputStream

    /**
     * a tooltip that will be displayed to the user
     * when hovering over the toolbar button
     */
    val tooltip : String

    /**
     * The actionHandler that will be invoked, once
     * the user clicks the toolbar button
     */
    val actionHandler: PluginActionHandler
}