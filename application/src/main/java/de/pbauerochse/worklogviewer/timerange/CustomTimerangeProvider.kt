package de.pbauerochse.worklogviewer.timerange

import de.pbauerochse.worklogviewer.report.TimeRange
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import java.time.LocalDate

/**
 * TimerangeProvider for a custom timerange where the start and end date is
 * provided by the user
 */
internal object CustomTimerangeProvider : TimerangeProvider {
    override val label: String = getFormatted("timerange.custom")
    override val settingsKey: String = "CUSTOM"
    override val isComputed: Boolean = false
    override fun buildTimeRange(start: LocalDate?, end: LocalDate?): TimeRange {
        checkNotNull(start) { getFormatted("timerange.custom.startrequired") }
        checkNotNull(end) { getFormatted("timerange.custom.endrequired") }
        return TimeRange(start, end)
    }
}
