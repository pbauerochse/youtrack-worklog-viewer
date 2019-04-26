package de.pbauerochse.worklogviewer.timerangeprovider

import de.pbauerochse.worklogviewer.domain.ReportTimerange
import de.pbauerochse.worklogviewer.domain.timerangeprovider.TimerangeProviderFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class TimerangeProviderFactoryTest {

    @Test
    fun performTest() {

        val now = LocalDate.now()

        for (reportTimerange in ReportTimerange.values()) {
            val timerangeProvider = TimerangeProviderFactory.getTimerangeProvider(reportTimerange, now, now)
            assertNotNull(timerangeProvider, "TimerangeProvider for timerange ${reportTimerange.name} was null")

            if (reportTimerange == ReportTimerange.CUSTOM) {
                assertEquals(now, timerangeProvider.timeRange.start)
                assertEquals(now, timerangeProvider.timeRange.end)
            }
        }
    }
}
