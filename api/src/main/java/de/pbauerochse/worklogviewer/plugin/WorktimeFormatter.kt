package de.pbauerochse.worklogviewer.plugin

interface WorktimeFormatter {

    fun getFormatted(durationInMinutes : Long, full : Boolean = false) : String

}