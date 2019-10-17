package de.pbauerochse.worklogviewer.timerange

import de.pbauerochse.worklogviewer.report.TimeRange
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import java.time.LocalDate

object CurrentAndLastWeekTimerangeProvider : TimerangeProvider {
    override val label: String = getFormatted("timerange.thisandlastweek")
    override val settingsKey: String = "THIS_AND_LAST_WEEK"
    override val isComputed: Boolean = true
    override fun buildTimeRange(start: LocalDate?, end: LocalDate?): TimeRange {
        return TimeRange.currentAndLastWeek()
    }
}
