package de.pbauerochse.worklogviewer.youtrack;

import de.pbauerochse.worklogviewer.youtrack.connector.YouTrackAuthenticationMethod;
import de.pbauerochse.worklogviewer.youtrack.createreport.request.CreateReportRequestEntity;
import de.pbauerochse.worklogviewer.youtrack.createreport.response.ReportDetailsResponse;
import de.pbauerochse.worklogviewer.youtrack.domain.GroupByCategory;
import de.pbauerochse.worklogviewer.youtrack.domain.WorklogReport;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Allows the interaction with the YouTrack API
 */
public interface YouTrackConnector {

    /**
     * Returns all available GroupByCategories
     */
    List<GroupByCategory> getPossibleGroupByCategories() throws Exception;

    ReportDetailsResponse createReport(CreateReportRequestEntity requestEntity) throws Exception;

    ReportDetailsResponse getReportDetails(String reportId) throws Exception;

    ByteArrayInputStream downloadReport(String reportId) throws Exception;

    void deleteReport(String reportId) throws Exception;

    YouTrackAuthenticationMethod getAuthenticationMethod();

    void fetchTaskDetails(WorklogReport report) throws Exception;
}
