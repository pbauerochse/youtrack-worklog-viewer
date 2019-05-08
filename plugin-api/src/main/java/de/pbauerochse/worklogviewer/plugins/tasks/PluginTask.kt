package de.pbauerochse.worklogviewer.plugins.tasks

import de.pbauerochse.worklogviewer.tasks.Progress

/**
 * An async task that might be executed via the [TaskRunner]
 */
interface PluginTask<T> {

    /**
     * The label of this task. Will be used in the progress
     * bar to distinguish multiple parallel tasks from another
     */
    val label : String

    /**
     * The actual work that will be performed
     * must be implemented in here
     */
    fun run(progress: Progress) : T?
}