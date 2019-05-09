package de.pbauerochse.worklogviewer.timerange

import de.pbauerochse.worklogviewer.report.TimeRange

import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import java.time.LocalDate

/**
 * TimeRange provider for the currrent month (including 1st till end of the month)
 */
internal object CurrentMonthTimerangeProvider : TimerangeProvider {
    override val label: String = getFormatted("timerange.thismonth")
    override val settingsKey: String = "THIS_MONTH"
    override val isComputed: Boolean = true
    override fun buildTimeRange(start: LocalDate?, end: LocalDate?): TimeRange {
        return TimeRange.currentMonth()
    }
}
