package de.pbauerochse.worklogviewer.plugin

import java.io.InputStream

interface PluginToolbarButton {
    val icon : InputStream
    val tooltip : String
    val actionHandler: PluginActionHandler
}