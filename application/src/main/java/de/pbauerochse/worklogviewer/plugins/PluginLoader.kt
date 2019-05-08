package de.pbauerochse.worklogviewer.plugins

import de.pbauerochse.worklogviewer.WorklogViewer
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.util.*

object PluginLoader {

    private val LOGGER = LoggerFactory.getLogger(PluginLoader::class.java)
    private val PLUGIN_DIRECTORY = File(System.getProperty("user.home"), ".youtrack-worklog-viewer/plugins")

    private var pluginClassloader: ClassLoader? = null

    @JvmStatic
    fun setScanForPlugins(pluginsEnabled: Boolean) {
        pluginClassloader = when (pluginsEnabled) {
            true -> pluginClassloader ?: createClassloader()
            else -> null
        }
    }

    @JvmStatic
    fun getPlugins(): List<WorklogViewerPlugin> {
        return pluginClassloader?.let {
            val loader = ServiceLoader.load(WorklogViewerPlugin::class.java, it)
            loader.reload()
            loader.toList()
        } ?: emptyList()
    }

    private fun createClassloader(): ClassLoader {
        val potentialPluginJars = pluginJars()
        LOGGER.info("Found ${potentialPluginJars.size} potential Plugins in ${PLUGIN_DIRECTORY.absolutePath}")
        return URLClassLoader(potentialPluginJars, WorklogViewer::class.java.classLoader)
    }

    private fun pluginJars(): Array<URL> = PLUGIN_DIRECTORY
        .listFiles { _, name -> name.toLowerCase().endsWith(".jar") }
        .map { it.toURI().toURL() }
        .toTypedArray()

}