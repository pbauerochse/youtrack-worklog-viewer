package de.pbauerochse.worklogviewer.timerange

import de.pbauerochse.worklogviewer.timereport.TimeRange
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import java.time.LocalDate

/**
 * The last month as [TimerangeProvider]
 */
internal object LastMonthTimerangeProvider : TimerangeProvider {
    override val label: String = getFormatted("timerange.lastmonth")
    override val settingsKey: String = "LAST_MONTH"
    override val isComputed: Boolean = true
    override fun buildTimeRange(start: LocalDate?, end: LocalDate?): TimeRange {
        return TimeRange.lastMonth()
    }
}