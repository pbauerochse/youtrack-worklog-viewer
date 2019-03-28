package de.pbauerochse.worklogviewer.plugin

interface PluginActionHandler {
    fun onAction(context: PluginActionContext)
}