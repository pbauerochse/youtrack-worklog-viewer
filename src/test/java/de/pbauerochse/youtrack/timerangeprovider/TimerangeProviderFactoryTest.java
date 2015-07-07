package de.pbauerochse.youtrack.timerangeprovider;

import de.pbauerochse.youtrack.domain.ReportTimerange;
import de.pbauerochse.youtrack.domain.TimerangeProvider;
import de.pbauerochse.youtrack.domain.timerangeprovider.TimerangeProviderFactory;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;

/**
 * @author Patrick Bauerochse
 * @since 07.07.15
 */
public class TimerangeProviderFactoryTest {

    @Test
    public void performTest() {

        LocalDate now = LocalDate.now();

        for (ReportTimerange reportTimerange : ReportTimerange.values()) {
            TimerangeProvider timerangeProvider = TimerangeProviderFactory.getTimerangeProvider(reportTimerange, now, now);
            Assert.assertNotNull("TimerangeProvider for timerange " + reportTimerange.name() + " was null", timerangeProvider);

            if (reportTimerange == ReportTimerange.CUSTOM) {
                Assert.assertEquals(now, timerangeProvider.getStartDate());
                Assert.assertEquals(now, timerangeProvider.getEndDate());
            }
        }
    }
}
