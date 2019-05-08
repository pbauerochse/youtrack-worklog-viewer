package de.pbauerochse.worklogviewer.report

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit

data class TimeRange(val start: LocalDate, val end: LocalDate) {

    val reportName: String = "${start.format(FORMATTER)}_${end.format(FORMATTER)}"

    override fun toString(): String {
        return "${start.format(FORMATTER)} - ${end.format(FORMATTER)}"
    }

    companion object {

        private val FORMATTER = DateTimeFormatter.ISO_DATE

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
            val now = LocalDate.now(ZoneId.systemDefault())

            val startDate = now.with(ChronoField.DAY_OF_WEEK, DayOfWeek.MONDAY.value.toLong())
            val endDate = now.with(ChronoField.DAY_OF_WEEK, DayOfWeek.SUNDAY.value.toLong())
            return TimeRange(startDate, endDate)
        }

        @JvmStatic
        fun lastWeek(): TimeRange {
            val now = LocalDate.now(ZoneId.systemDefault())
            val lastWeek = now.minus(1, ChronoUnit.WEEKS)

            val startDate = lastWeek.with(ChronoField.DAY_OF_WEEK, DayOfWeek.MONDAY.value.toLong())
            val endDate = lastWeek.with(ChronoField.DAY_OF_WEEK, DayOfWeek.SUNDAY.value.toLong())
            return TimeRange(startDate, endDate)
        }

    }
}
