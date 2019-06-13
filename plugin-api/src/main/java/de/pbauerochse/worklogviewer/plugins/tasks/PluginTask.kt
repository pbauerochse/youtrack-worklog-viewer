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
     * Determines whether the main application window
     * should block user input while this task is running
     * e.g. show the loading spinner
     */
    val isBlockingUi : Boolean

    /**
     * The actual work that will be performed
     * must be implemented in here
     */
    fun run(progress: Progress) : T?
}