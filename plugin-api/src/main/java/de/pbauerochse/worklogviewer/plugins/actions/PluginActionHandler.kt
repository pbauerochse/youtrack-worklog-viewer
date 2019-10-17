package de.pbauerochse.worklogviewer.plugins.actions

/**
 * Will be invoked once the corresponding [PluginToolbarButton]
 * or [PluginMenuItem] was pressed by the user
 */
interface PluginActionHandler {

    /**
     * @param context   a predefined context, representing the state of the application, including additional tools to interact with the application
     */
    fun onAction(context: PluginActionContext)

}