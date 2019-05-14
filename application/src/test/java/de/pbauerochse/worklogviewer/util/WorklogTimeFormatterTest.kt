package de.pbauerochse.worklogviewer.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class WorklogTimeFormatterTest {

    @Test
    fun `validate simple with full hours`() {
        val eightHourDayFormatter = WorklogTimeFormatter(8.0f)

        assertEquals("1d", eightHourDayFormatter.getFormatted(8* 60))
        assertEquals("1d 30m", eightHourDayFormatter.getFormatted(8 * 60 + 30))
        assertEquals("45m", eightHourDayFormatter.getFormatted(45))
        assertEquals("1h 30m", eightHourDayFormatter.getFormatted(90))
        assertEquals("1d 1h 30m", eightHourDayFormatter.getFormatted(8 * 60 + 90))
    }

    @Test
    fun `validate full with full hours`() {
        val eightHourDayFormatter = WorklogTimeFormatter(8.0f)

        assertEquals("1d 0h 0m", eightHourDayFormatter.getFormatted(8* 60, true))
        assertEquals("1d 0h 30m", eightHourDayFormatter.getFormatted(8 * 60 + 30, true))
        assertEquals("45m", eightHourDayFormatter.getFormatted(45, true))
        assertEquals("1h 30m", eightHourDayFormatter.getFormatted(90, true))
        assertEquals("1d 1h 30m", eightHourDayFormatter.getFormatted(8 * 60 + 90, true))
    }

    @Test
    fun `validate simple with decimal hours`() {
        val eightHourDayFormatter = WorklogTimeFormatter(9.5f)

        assertEquals("1d", eightHourDayFormatter.getFormatted(570))
        assertEquals("1d 30m", eightHourDayFormatter.getFormatted(600))
        assertEquals("45m", eightHourDayFormatter.getFormatted(45))
        assertEquals("1h 30m", eightHourDayFormatter.getFormatted(90))
        assertEquals("1d 1h 30m", eightHourDayFormatter.getFormatted(660))
    }

    @Test
    fun `validate full with decimal hours`() {
        val eightHourDayFormatter = WorklogTimeFormatter(9.5f)

        assertEquals("1d 0h 0m", eightHourDayFormatter.getFormatted(570, true))
        assertEquals("1d 0h 30m", eightHourDayFormatter.getFormatted(600, true))
        assertEquals("45m", eightHourDayFormatter.getFormatted(45, true))
        assertEquals("1h 30m", eightHourDayFormatter.getFormatted(90, true))
        assertEquals("1d 1h 30m", eightHourDayFormatter.getFormatted(660, true))
    }

}
