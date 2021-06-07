package de.pbauerochse.worklogviewer.timerange

import de.pbauerochse.worklogviewer.timereport.TimeRange
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import java.time.LocalDate

/**
 * The current week as [TimerangeProvider]
 */
internal object CurrentWeekTimerangeProvider : TimerangeProvider {
    override val label: String = getFormatted("timerange.thisweek")
    override val settingsKey: String = "THIS_WEEK"
    override val isComputed: Boolean = true
    override fun buildTimeRange(start: LocalDate?, end: LocalDate?): TimeRange {
        return TimeRange.currentWeek()
    }
}
