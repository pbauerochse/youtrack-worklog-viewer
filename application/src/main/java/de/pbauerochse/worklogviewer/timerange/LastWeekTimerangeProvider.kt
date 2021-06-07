package de.pbauerochse.worklogviewer.timerange

import de.pbauerochse.worklogviewer.timereport.TimeRange
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import java.time.LocalDate

/**
 * The previous week as [TimerangeProvider]
 */
internal object LastWeekTimerangeProvider : TimerangeProvider {
    override val label: String = getFormatted("timerange.lastweek")
    override val settingsKey: String = "LAST_WEEK"
    override val isComputed: Boolean = true
    override fun buildTimeRange(start: LocalDate?, end: LocalDate?): TimeRange {
        return TimeRange.lastWeek()
    }
}
