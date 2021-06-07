package de.pbauerochse.worklogviewer.timereport

import de.pbauerochse.worklogviewer.isSameDayOrAfter
import de.pbauerochse.worklogviewer.isSameDayOrBefore
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import java.util.*

data class TimeRange(val start: LocalDate, val end: LocalDate) : Iterable<LocalDate> {

    val reportName: String = "${start.format(ISO_DATE_FORMATTER)}_${end.format(ISO_DATE_FORMATTER)}"
    val formattedForLocale: String = "${start.format(LOCALIZED_FORMATTER)} - ${end.format(LOCALIZED_FORMATTER)}"

    override fun toString(): String {
        return "${start.format(ISO_DATE_FORMATTER)} - ${end.format(ISO_DATE_FORMATTER)}"
    }

    fun includes(date: LocalDate): Boolean = date.isSameDayOrAfter(start) && date.isSameDayOrBefore(end)

    /**
     * Returns the total number of days for this timerange.
     * Start and End are both counted as full days (inclusive)
     */
    val totalNumberOfDays : Int
        get() = Period.between(start, end.plusDays(1)).days

    /**
     * Iterates over all days covered by this timerange
     */
    override fun iterator(): Iterator<LocalDate> {
        return TimeRangeIterator(start, end)
    }

    private class TimeRangeIterator(start : LocalDate, private val end : LocalDate) : Iterator<LocalDate> {

        private var nextValue : LocalDate?

        init {
            require(start.isSameDayOrBefore(end)) { "End must not be before Start" }
            nextValue = start
        }

        override fun hasNext(): Boolean = nextValue != null
        override fun next(): LocalDate {
            val currentValue = nextValue!!
            nextValue = currentValue.plusDays(1).takeIf { it.isSameDayOrBefore(end) }
            return currentValue
        }
    }

    companion object {

        private val ISO_DATE_FORMATTER = DateTimeFormatter.ISO_DATE
        private val LOCALIZED_FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)

        @JvmStatic
        fun currentMonth(): TimeRange {
            val now = LocalDate.now(ZoneId.systemDefault())
            val startDate = now.withDayOfMonth(1)
            val endDate = now.withDayOfMonth(now.month.length(now.isLeapYear))

            return TimeRange(startDate, endDate)
        }

        @JvmStatic
        fun lastMonth(): TimeRange {
            val now = LocalDate.now(ZoneId.systemDefault())
            val lastMonth = now.minus(1, ChronoUnit.MONTHS)

            val firstDayOfMonth = lastMonth.withDayOfMonth(1)
            val lastDayOfMonth = lastMonth.withDayOfMonth(lastMonth.month.length(lastMonth.isLeapYear))
            return TimeRange(firstDayOfMonth, lastDayOfMonth)
        }

        @JvmStatic
        fun currentWeek(): TimeRange {
            val weekFields = WeekFields.of(Locale.getDefault())
            val firstDayOfTheWeekForLocale = weekFields.firstDayOfWeek
            val lastDayOfTheWeekForLocale = firstDayOfTheWeekForLocale.plus(6)

            val now = LocalDate.now(ZoneId.systemDefault())

            val endDate = now.with(ChronoField.DAY_OF_WEEK, lastDayOfTheWeekForLocale.value.toLong())
            val startDateCandidate = now.with(ChronoField.DAY_OF_WEEK, firstDayOfTheWeekForLocale.value.toLong())
            val startDate = if (startDateCandidate.isBefore(endDate)) startDateCandidate else startDateCandidate.minusWeeks(1)
            return TimeRange(startDate, endDate)
        }

        @JvmStatic
        fun lastWeek(): TimeRange {
            val weekFields = WeekFields.of(Locale.getDefault())
            val firstDayOfTheWeekForLocale = weekFields.firstDayOfWeek
            val lastDayOfTheWeekForLocale = firstDayOfTheWeekForLocale.plus(6)

            val now = LocalDate.now(ZoneId.systemDefault())
            val lastWeek = now.minus(1, ChronoUnit.WEEKS)

            val startDateCandidate = lastWeek.with(ChronoField.DAY_OF_WEEK, firstDayOfTheWeekForLocale.value.toLong())

            val endDate = lastWeek.with(ChronoField.DAY_OF_WEEK, lastDayOfTheWeekForLocale.value.toLong())
            val startDate = if (startDateCandidate.isBefore(endDate)) startDateCandidate else startDateCandidate.minusWeeks(1)
            return TimeRange(startDate, endDate)
        }

        @JvmStatic
        fun lastTwoWeeks(): TimeRange {
            val lastWeek = lastWeek()
            val start = lastWeek.start.minusWeeks(1)
            val end = lastWeek.end
            return TimeRange(start, end)
        }

        @JvmStatic
        fun currentAndLastWeek(): TimeRange {
            val start = lastWeek().start
            val end = currentWeek().end
            return TimeRange(start, end)
        }

    }
}
