package de.pbauerochse.worklogviewer.util

import de.pbauerochse.worklogviewer.plugins.formatter.YouTrackWorktimeFormatter

class WorklogTimeFormatter(private val workhoursADay: Float) : YouTrackWorktimeFormatter {

    init {
        require(workhoursADay > 0) { FormattingUtil.getFormatted("exceptions.main.workhours.zero") }
    }

    override fun getFormatted(durationInMinutes: Long, full: Boolean): String {
        val worklogFormatted = StringBuilder()

        val minutesPerWorkday = (workhoursADay * MINUTES_PER_HOUR).toLong()

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

    override fun parseDurationInMinutes(value : String) : Long? {
        val matchGroup = WORKTIME_PATTERN.matchEntire(value)

        val days : Long? = matchGroup?.groupValues?.get(2)?.takeIf { it.isNotBlank() }?.toLong()
        val hours : Long? = matchGroup?.groupValues?.get(4)?.takeIf { it.isNotBlank() }?.toLong()
        val minutes : Long? = matchGroup?.groupValues?.get(6)?.takeIf { it.isNotBlank() }?.toLong()

        return if (days == null && hours == null && minutes == null) {
            null
        } else {
            val daysInMinutes = ((days ?: 0) * workhoursADay).toLong() * MINUTES_PER_HOUR
            val hoursInMinutes = (hours ?: 0) * MINUTES_PER_HOUR
            val minutesSanitized = minutes ?: 0
            return daysInMinutes + hoursInMinutes + minutesSanitized
        }
    }

    companion object {
        private const val MINUTES_PER_HOUR = 60
        private val WORKTIME_PATTERN = Regex("((\\d+)d)?\\s*((\\d+)h)?\\s*((\\d+)m)?")
    }
}
