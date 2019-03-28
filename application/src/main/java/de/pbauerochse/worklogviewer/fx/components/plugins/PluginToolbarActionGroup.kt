package de.pbauerochse.worklogviewer.fx.components.plugins

import de.pbauerochse.worklogviewer.plugin.PluginActionContext
import de.pbauerochse.worklogviewer.plugin.PluginToolbarButton
import de.pbauerochse.worklogviewer.plugin.WorklogViewerPlugin
import javafx.event.EventHandler
import javafx.scene.control.Button
import javafx.scene.control.Tooltip
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import org.slf4j.LoggerFactory

class PluginToolbarActionGroup(private val plugin: WorklogViewerPlugin, private val pluginActionContext: PluginActionContext) : HBox() {

    init {
        isVisible = plugin.toolbarButtons.isNotEmpty()
        addToolbarButtons()
    }

    private fun addToolbarButtons() {
        plugin.toolbarButtons.forEach { toolbarButton ->
            LOGGER.info("Adding Toolbar Button ${plugin.name} -> ${toolbarButton.tooltip}")
            val button = Button().apply {
                prefHeight = 26.0
                prefWidth = 26.0
                tooltip = Tooltip("${toolbarButton.tooltip} (${plugin.name})")
                onAction = EventHandler { triggerToolbarAction(toolbarButton) }
                graphic = ImageView(Image(toolbarButton.icon))
            }
            children.add(button)
        }
    }

    private fun triggerToolbarAction(pluginToolbarButton: PluginToolbarButton) {
        LOGGER.debug("Plugin Toolbar Action triggered: ${plugin.name} -> ${pluginToolbarButton.tooltip}")
        pluginToolbarButton.actionHandler.onAction(pluginActionContext)
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(PluginToolbarActionGroup::class.java)
    }

}