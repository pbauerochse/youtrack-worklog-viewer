package de.pbauerochse.worklogviewer.fx.components.plugins

import de.pbauerochse.worklogviewer.fx.dialog.Dialog
import de.pbauerochse.worklogviewer.plugins.WorklogViewerPlugin
import de.pbauerochse.worklogviewer.plugins.actions.PluginActionContext
import de.pbauerochse.worklogviewer.plugins.actions.PluginMenuItem
import de.pbauerochse.worklogviewer.plugins.dialog.DialogSpecification
import de.pbauerochse.worklogviewer.util.FormattingUtil
import javafx.event.EventHandler
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import org.slf4j.LoggerFactory

/**
 * The plugin menu submenu for the given plugin. Contains at least
 * a single MenuItem, that show details about the plugin in a popup.
 *
 * Will also contain all MenuItems for each [WorklogViewerPlugin.menuItems]
 */
class PluginMenu(private val plugin: WorklogViewerPlugin, private val pluginContextFactory: () -> PluginActionContext) : Menu(plugin.name) {

    init {
        addPluginActions()
        addInfoAction()
    }

    private fun addPluginActions() {
        plugin.menuItems.forEach { pluginMenuItem ->
            val menuItem = MenuItem(pluginMenuItem.name)
            menuItem.onAction = EventHandler { triggerMenuItemAction(pluginMenuItem) }
            items.add(menuItem)
        }
    }

    private fun addInfoAction() {
        if (plugin.menuItems.isNotEmpty()) {
            items.add(SeparatorMenuItem())
        }

        val pluginInfoMenuItem = MenuItem(FormattingUtil.getFormatted("plugins.info"))
        pluginInfoMenuItem.onAction = EventHandler { showPluginInfoPopup() }
        items.add(pluginInfoMenuItem)
    }

    private fun triggerMenuItemAction(pluginMenuItem: PluginMenuItem) {
        LOGGER.debug("Plugin Action triggered: ${plugin.name} -> ${pluginMenuItem.name}")
        pluginMenuItem.actionHandler.onAction(pluginContextFactory.invoke())
    }

    private fun showPluginInfoPopup() {
        LOGGER.info("Showing Plugin Popup for ${plugin.name}")
        Dialog(parentPopup.scene).openDialog(PluginDetailPopupContent(plugin), DialogSpecification(plugin.name))
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(PluginMenu::class.java)
    }

}