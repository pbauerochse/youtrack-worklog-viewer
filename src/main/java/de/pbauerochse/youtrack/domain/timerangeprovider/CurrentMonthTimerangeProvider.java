package de.pbauerochse.youtrack.domain.timerangeprovider;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * @author Patrick Bauerochse
 * @since 13.04.15
 */
public class CurrentMonthTimerangeProvider extends BaseTimerangeProvider {

    @Override
    protected void initialize() {
        LocalDate now = LocalDate.now(ZoneId.systemDefault());
        startDate = now.withDayOfMonth(1);
        endDate = now.withDayOfMonth(now.getMonth().length(now.isLeapYear()));
    }
}
