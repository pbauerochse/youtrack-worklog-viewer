package de.pbauerochse.worklogviewer.report

import de.pbauerochse.worklogviewer.isSameDayOrAfter
import de.pbauerochse.worklogviewer.isSameDayOrBefore
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import java.util.*

data class TimeRange(val start: LocalDate, val end: LocalDate) {

    val reportName: String = "${start.format(ISO_DATE_FORMATTER)}_${end.format(ISO_DATE_FORMATTER)}"
    val formattedForLocale: String = "${start.format(LOCALIZED_FORMATTER)} - ${end.format(LOCALIZED_FORMATTER)}"

    override fun toString(): String {
        return "${start.format(ISO_DATE_FORMATTER)} - ${end.format(ISO_DATE_FORMATTER)}"
    }

    fun isIncluded(date: LocalDate): Boolean {
        return date.isSameDayOrAfter(start) && date.isSameDayOrBefore(end)
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

            // TODO use first and last day of week instead of hardcoding it

            val now = LocalDate.now(ZoneId.systemDefault())

            val startDate = now.with(ChronoField.DAY_OF_WEEK, DayOfWeek.MONDAY.value.toLong())
            val endDate = now.with(ChronoField.DAY_OF_WEEK, DayOfWeek.SUNDAY.value.toLong())
            return TimeRange(startDate, endDate)
        }

        @JvmStatic
        fun lastWeek(): TimeRange {
            val weekFields = WeekFields.of(Locale.getDefault())
            val firstDayOfTheWeekForLocale = weekFields.firstDayOfWeek
            val lastDayOfTheWeekForLocale = firstDayOfTheWeekForLocale.plus(6)

            // TODO use first and last day of week instead of hardcoding it

            val now = LocalDate.now(ZoneId.systemDefault())
            val lastWeek = now.minus(1, ChronoUnit.WEEKS)

            val startDate = lastWeek.with(ChronoField.DAY_OF_WEEK, DayOfWeek.MONDAY.value.toLong())
            val endDate = lastWeek.with(ChronoField.DAY_OF_WEEK, DayOfWeek.SUNDAY.value.toLong())
            return TimeRange(startDate, endDate)
        }

    }
}
