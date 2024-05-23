package de.pbauerochse.worklogviewer.datasource.api

import de.pbauerochse.worklogviewer.datasource.api.domain.ProjectTimeTrackingSettings
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.Future

/**
 * [ProjectTimeTrackingSettings] are very rarely subject to change so
 * I assume they are safe to cache to speed up loading on subsequent
 * time report calls
 */
object ProjectTimeSettingsCache {

    private val LOGGER = LoggerFactory.getLogger(ProjectTimeSettingsCache::class.java)!!
    private val cache: ConcurrentMap<String, Future<ProjectTimeTrackingSettings>> = ConcurrentHashMap()

    fun forProject(projectId: String, loadSettings: () -> ProjectTimeTrackingSettings): Future<ProjectTimeTrackingSettings> {
        LOGGER.debug("Requested TimeSettings for Project {}", projectId)
        return cache.computeIfAbsent(projectId) { CompletableFuture.supplyAsync {
            LOGGER.info("Retrieving TimeSettings for Project {}", projectId)
            loadSettings.invoke()
        } }
    }
}