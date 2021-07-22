package de.pbauerochse.worklogviewer.datasource.api

import de.pbauerochse.worklogviewer.datasource.api.domain.ProjectTimeTrackingSettings

/**
 * [ProjectTimeTrackingSettings] are very rarely subject to change so
 * I assume they are safe to cache to speed up loading on subsequent
 * time report calls
 */
object ProjectTimeSettingsCache {

    private val cache = mutableMapOf<String, ProjectTimeTrackingSettings>()

    fun forProject(projectId: String, loadSettings: () -> ProjectTimeTrackingSettings): ProjectTimeTrackingSettings {
        return cache.computeIfAbsent(projectId) { loadSettings.invoke() }
    }

}