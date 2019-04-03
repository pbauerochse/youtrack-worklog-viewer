package de.pbauerochse.worklogviewer.logging

import javafx.concurrent.ScheduledService
import javafx.concurrent.Task

/**
 * ScheduleService that publishes pending Log Messages
 * to all registered listeners as batches to avoid the UI freezing
 * when there are a lot of messages being logged
 */
class ProcessPendingLogsService : ScheduledService<Unit>() {

    override fun createTask(): Task<Unit> {
        return object : Task<Unit>() {
            override fun call() = WorklogViewerLogs.notifyListeners()
        }
    }
}