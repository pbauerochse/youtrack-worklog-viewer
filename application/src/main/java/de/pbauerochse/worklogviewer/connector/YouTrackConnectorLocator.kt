package de.pbauerochse.worklogviewer.connector

import de.pbauerochse.worklogviewer.WorklogViewer
import de.pbauerochse.worklogviewer.settings.SettingsUtil
import java.util.*

/**
 * Uses the Java ServiceLoader to detect
 * implementations of the YouTrackConnectorFactory
 */
object YouTrackConnectorLocator {

    @JvmStatic
    fun getActiveConnector(): YouTrackConnector? {
        val definedVersion = SettingsUtil.settings.youTrackConnectionSettings.version
        val youtrackSettings = SettingsUtil.settings.youTrackConnectionSettings

        return getConnectorFactoryForVersion(definedVersion)?.createServiceInstance(youtrackSettings)
    }

    @JvmStatic
    fun getSupportedVersions(): List<YouTrackVersion> {
        return getAvailableConnectorFactories()
            .flatMap { it.supportedVersions }
            .distinct()
            .sorted()
    }

    private fun getConnectorFactoryForVersion(version: YouTrackVersion?): YouTrackConnectorFactory? {
        if (version == null) {
            return null
        }

        return getAvailableConnectorFactories().find { it.supportedVersions.contains(version) }
    }

    private fun getAvailableConnectorFactories(): Iterable<YouTrackConnectorFactory> {
        val loader = ServiceLoader.load(YouTrackConnectorFactory::class.java, WorklogViewer::class.java.classLoader)
        loader.reload()
        return loader
    }

}