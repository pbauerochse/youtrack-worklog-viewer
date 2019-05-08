package de.pbauerochse.worklogviewer.plugins

import de.pbauerochse.worklogviewer.plugins.actions.PluginMenuItem
import de.pbauerochse.worklogviewer.plugins.actions.PluginToolbarButton
import de.pbauerochse.worklogviewer.version.Version

/**
 * Main interface to implement if you want
 * to provide your own Worklog Viewer Plugin
 */
interface WorklogViewerPlugin {

    /**
     * The name of this plugin. Will be used in the plugins menu
     * as well as in the plugin info dialog
     */
    val name : String

    /**
     * a description on what the plugin provides
     */
    val description : String

    /**
     * the version of this plugin
     */
    val version : Version

    /**
     * the author / developer of this plugin
     */
    val author : Author

    /**
     * a list of [PluginMenuItem]s that this plugin provides
     * If there should be no menu items for this plugin,
     * return an empty list
     */
    val menuItems : List<PluginMenuItem>

    /**
     * a list of [PluginToolbarButton]s that this plugin provides.
     * If there should be no toolbar buttons for this plugin,
     * return an empty list
     */
    val toolbarButtons : List<PluginToolbarButton>
}