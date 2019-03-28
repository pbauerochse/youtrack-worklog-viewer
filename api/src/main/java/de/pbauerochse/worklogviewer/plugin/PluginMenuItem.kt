package de.pbauerochse.worklogviewer.plugin

interface PluginMenuItem {
    val name: String
    val actionHandler: PluginActionHandler
}