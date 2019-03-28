package de.pbauerochse.worklogviewer.fx.components.plugins

import de.pbauerochse.worklogviewer.WorklogViewer
import de.pbauerochse.worklogviewer.plugin.WorklogViewerPlugin
import de.pbauerochse.worklogviewer.setHref
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import java.net.URL
import java.util.*

/**
 * Displays basic information about the plugin
 */
class PluginDetailPopupContent(private val plugin: WorklogViewerPlugin) : VBox(), Initializable {

    @FXML
    private lateinit var pluginNameLabel: Label

    @FXML
    private lateinit var pluginVersionLabel: Label

    @FXML
    private lateinit var pluginVendorLink: Hyperlink

    @FXML
    private lateinit var pluginDescriptionLabel: Label

    init {
        val loader = FXMLLoader(WorklogViewer::class.java.getResource("/fx/views/plugin-details.fxml"))
        loader.setRoot(this)
        loader.setController(this)
        loader.load<PluginDetailPopupContent>()
    }

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        pluginNameLabel.text = plugin.name
        pluginVersionLabel.text = plugin.version.toString()
        pluginVendorLink.text = plugin.vendor.name
        pluginDescriptionLabel.text = plugin.description

        plugin.vendor.website?.let {
            pluginVendorLink.setHref(it.toExternalForm())
        }
    }
}