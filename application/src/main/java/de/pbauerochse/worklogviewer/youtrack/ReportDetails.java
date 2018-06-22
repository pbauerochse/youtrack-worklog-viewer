package de.pbauerochse.worklogviewer.youtrack;

@Deprecated
public interface ReportDetails {

    String getReportId();

    boolean isRecomputing();

    boolean isReady();

}
