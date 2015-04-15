package de.pbauerochse.youtrack.domain.timerangeprovider;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoField;

/**
 * @author Patrick Bauerochse
 * @since 13.04.15
 */
public class CurrentWeekTimerangeProvider extends BaseTimerangeProvider {

    @Override
    protected void initialize() {
        LocalDate now = LocalDate.now(ZoneId.systemDefault());

        startDate = now.with(ChronoField.DAY_OF_WEEK, DayOfWeek.MONDAY.getValue());
        endDate = now.with(ChronoField.DAY_OF_WEEK, DayOfWeek.SUNDAY.getValue());
    }
}