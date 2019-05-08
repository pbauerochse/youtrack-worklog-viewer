package de.pbauerochse.worklogviewer.plugins.actions

/**
 * Represents an menu item in the plugins menu of the main
 * application. When the user clicks on the specific menu
 * item, the actionHandler will be invoked by the main application
 */
interface PluginMenuItem {

    /**
     * The name / label of the menu item
     */
    val name: String

    /**
     * The actionHandler that will be invoked when the menu item is clicked
     */
    val actionHandler: PluginActionHandler

}