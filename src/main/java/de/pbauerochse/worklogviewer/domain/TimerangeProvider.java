package de.pbauerochse.worklogviewer.domain;

import java.time.LocalDate;

/**
 * Provides the start- and enddate
 * for a given timerange
 */
public interface TimerangeProvider {

    LocalDate getStartDate();

    LocalDate getEndDate();

    ReportTimerange getReportTimerange();

}
