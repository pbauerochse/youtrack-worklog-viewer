package de.pbauerochse.worklogviewer.timerangeprovider

import de.pbauerochse.worklogviewer.report.TimeRange
import de.pbauerochse.worklogviewer.timerange.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.random.Random

internal class TimerangeProvidersTest {

    private lateinit var start: LocalDate
    private lateinit var end: LocalDate

    @BeforeEach
    internal fun setup() {
        start = LocalDate.now().minusDays(Random.nextLong(5, 50))
        end = LocalDate.now().plusDays(Random.nextLong(5, 50))
    }

    @Test
    internal fun testCustomTimerangeProvider() {
        val timerange = CustomTimerangeProvider.buildTimeRange(start, end)

        assertEquals(start, timerange.start)
        assertEquals(end, timerange.end)
    }

    @Test
    internal fun testThisWeekProvider() {
        val expectedTimerange = TimeRange.currentWeek()

        val timerange = CurrentWeekTimerangeProvider.buildTimeRange(start, end)

        assertEquals(expectedTimerange.start, timerange.start)
        assertEquals(expectedTimerange.end, timerange.end)
    }

    @Test
    internal fun testLastWeekProvider() {
        val expectedTimerange = TimeRange.lastWeek()

        val timerange = LastWeekTimerangeProvider.buildTimeRange(start, end)

        assertEquals(expectedTimerange.start, timerange.start)
        assertEquals(expectedTimerange.end, timerange.end)
    }

    @Test
    internal fun testThisMonthProvider() {
        val expectedTimerange = TimeRange.currentMonth()

        val timerange = CurrentMonthTimerangeProvider.buildTimeRange(start, end)

        assertEquals(expectedTimerange.start, timerange.start)
        assertEquals(expectedTimerange.end, timerange.end)
    }

    @Test
    internal fun testLastMonthProvider() {
        val expectedTimerange = TimeRange.lastMonth()

        val timerange = LastMonthTimerangeProvider.buildTimeRange(start, end)

        assertEquals(expectedTimerange.start, timerange.start)
        assertEquals(expectedTimerange.end, timerange.end)
    }
}
