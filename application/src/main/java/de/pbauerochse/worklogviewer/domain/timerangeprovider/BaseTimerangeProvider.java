package de.pbauerochse.worklogviewer.domain.timerangeprovider;

import de.pbauerochse.worklogviewer.domain.TimerangeProvider;

import java.time.LocalDate;

/**
 * @author Patrick Bauerochse
 * @since 13.04.15
 */
public abstract class BaseTimerangeProvider implements TimerangeProvider {

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof TimerangeProvider)) return false;

        TimerangeProvider other = (TimerangeProvider) obj;

        LocalDate thisStart = getTimeRange().getStart();
        LocalDate otherStart = other.getTimeRange().getStart();

        LocalDate thisEnd = getTimeRange().getEnd();
        LocalDate otherEnd = other.getTimeRange().getEnd();

        return other.getReportTimerange() == getReportTimerange() && thisStart.isEqual(otherStart) && thisEnd.isEqual(otherEnd);
    }

    @Override
    public int hashCode() {
        int result = getTimeRange().getStart().hashCode();
        result = 31 * result + getTimeRange().getEnd().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return getTimeRange().getReportName();
    }
}
