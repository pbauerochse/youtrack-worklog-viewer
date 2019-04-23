package de.pbauerochse.worklogviewer.util

import de.pbauerochse.worklogviewer.plugin.WorktimeFormatter

class WorklogTimeFormatter(private val workhoursADay: Int) : WorktimeFormatter {

    init {
        require(workhoursADay > 0) { FormattingUtil.getFormatted("exceptions.main.workhours.zero") }
    }

    override fun getFormatted(durationInMinutes: Long, full: Boolean): String {
        val worklogFormatted = StringBuilder()

        val minutesPerWorkday = workhoursADay * MINUTES_PER_HOUR

        val days = durationInMinutes / minutesPerWorkday
        var remainingMinutes = durationInMinutes % minutesPerWorkday

        val hours = remainingMinutes / MINUTES_PER_HOUR
        remainingMinutes %= MINUTES_PER_HOUR

        if (days > 0) {
            worklogFormatted.append(days).append('d')
        }

        if (hours > 0 || full && days > 0) {
            if (worklogFormatted.isNotEmpty()) {
                worklogFormatted.append(' ')
            }

            worklogFormatted.append(hours).append('h')
        }

        if (remainingMinutes > 0 || full && (hours > 0 || days > 0)) {
            if (worklogFormatted.isNotEmpty()) {
                worklogFormatted.append(' ')
            }

            worklogFormatted.append(remainingMinutes).append('m')
        }

        return worklogFormatted.toString()
    }

    companion object {
        private const val MINUTES_PER_HOUR = 60
    }
}