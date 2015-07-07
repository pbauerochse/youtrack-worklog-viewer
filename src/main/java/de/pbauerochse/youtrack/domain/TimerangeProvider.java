package de.pbauerochse.youtrack.domain;

import java.time.LocalDate;

/**
 * @author Patrick Bauerochse
 * @since 13.04.15
 */
public interface TimerangeProvider {

    LocalDate getStartDate();

    LocalDate getEndDate();

    ReportTimerange getReportTimerange();

}
