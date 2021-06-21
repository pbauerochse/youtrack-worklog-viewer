package de.pbauerochse.worklogviewer.preloader

import de.pbauerochse.worklogviewer.tasks.Tasks

object Preloader {

    fun preload() {
        Tasks.startBackgroundTask(PreloadTask())
    }

}