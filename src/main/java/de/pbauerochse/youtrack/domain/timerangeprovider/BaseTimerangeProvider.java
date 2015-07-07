package de.pbauerochse.youtrack.domain.timerangeprovider;

import de.pbauerochse.youtrack.domain.TimerangeProvider;

import java.time.LocalDate;

/**
 * @author Patrick Bauerochse
 * @since 13.04.15
 */
public abstract class BaseTimerangeProvider implements TimerangeProvider {

    protected LocalDate startDate;
    protected LocalDate endDate;

    @Override
    public LocalDate getStartDate() {
        return startDate;
    }

    @Override
    public LocalDate getEndDate() {
        return endDate;
    }
}
