package de.pbauerochse.worklogviewer.plugin

import de.pbauerochse.worklogviewer.version.Version

/**
 * Main interface to implement if you want
 * to provide your own Worklog Viewer Plugin
 */
interface WorklogViewerPlugin {

    val name : String
    val description : String
    val version : Version
    val vendor : Vendor

    val menuItems : List<PluginMenuItem>
    val toolbarButtons : List<PluginToolbarButton>

}