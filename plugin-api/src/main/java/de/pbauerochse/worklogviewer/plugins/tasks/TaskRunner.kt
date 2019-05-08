package de.pbauerochse.worklogviewer.plugins.tasks

/**
 * Allows starting a task to asynchronously
 * process some data
 */
interface TaskRunner {

    /**
     * starts the given task and invokes the optional callback when done
     */
    fun <T> start(task : PluginTask<T>, callback : TaskCallback<T>? = null)

}