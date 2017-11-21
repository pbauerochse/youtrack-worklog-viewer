package de.pbauerochse.worklogviewer.youtrack;

public interface ReportDetails {

    String getReportId();

    boolean isRecomputing();

    boolean isReady();

}
