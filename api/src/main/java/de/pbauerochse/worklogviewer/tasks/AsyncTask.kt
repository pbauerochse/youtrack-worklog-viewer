package de.pbauerochse.worklogviewer.tasks

interface AsyncTask<T> {
    val label : String
    fun run(progressCallback: ProgressCallback) : T?
}