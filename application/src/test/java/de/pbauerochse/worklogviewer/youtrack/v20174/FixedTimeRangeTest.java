package de.pbauerochse.worklogviewer.youtrack.v20174;

import org.junit.Test;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class FixedTimeRangeTest {

    @Test
    public void datesAreEpochMillisAtUTC() {
        FixedTimeRange timeRange = new FixedTimeRange(LocalDate.of(2018, 4, 1), LocalDate.of(2018, 4, 30));
        assertThat(timeRange.getFrom(), is(1522540800000L));
        assertThat(timeRange.getTo(), is(1525132799999L));
    }

}