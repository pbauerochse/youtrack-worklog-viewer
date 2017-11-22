package de.pbauerochse.worklogviewer.domain.timerangeprovider;

import de.pbauerochse.worklogviewer.domain.TimerangeProvider;

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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof TimerangeProvider)) return false;

        TimerangeProvider other = (TimerangeProvider) obj;
        return other.getReportTimerange() == getReportTimerange() && startDate.isEqual(other.getStartDate()) && endDate.isEqual(other.getEndDate());
    }

    @Override
    public int hashCode() {
        int result = startDate.hashCode();
        result = 31 * result + endDate.hashCode();
        return result;
    }
}
