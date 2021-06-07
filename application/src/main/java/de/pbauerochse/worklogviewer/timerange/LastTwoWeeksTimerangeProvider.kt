package de.pbauerochse.worklogviewer.timerange

import de.pbauerochse.worklogviewer.timereport.TimeRange
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import java.time.LocalDate

/**
 * TimerangeProvider for the last week and the week before that
 */
object LastTwoWeeksTimerangeProvider : TimerangeProvider {
    override val label: String = getFormatted("timerange.lasttwoweeks")
    override val settingsKey: String = "LAST_TWO_WEEKS"
    override val isComputed: Boolean = true
    override fun buildTimeRange(start: LocalDate?, end: LocalDate?): TimeRange {
        return TimeRange.lastTwoWeeks()
    }
}
