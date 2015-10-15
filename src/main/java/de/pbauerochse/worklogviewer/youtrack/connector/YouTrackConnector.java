package de.pbauerochse.worklogviewer.youtrack.connector;

import de.pbauerochse.worklogviewer.youtrack.createreport.request.CreateReportRequestEntity;
import de.pbauerochse.worklogviewer.youtrack.createreport.response.ReportDetailsResponse;
import de.pbauerochse.worklogviewer.youtrack.domain.GroupByCategory;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * @author Patrick Bauerochse
 * @since 01.04.15
 */
public interface YouTrackConnector {

    List<GroupByCategory> getPossibleGroupByCategories() throws Exception;

    ReportDetailsResponse createReport(CreateReportRequestEntity requestEntity) throws Exception;

    ReportDetailsResponse getReportDetails(String reportId) throws Exception;

    ByteArrayInputStream downloadReport(String reportId) throws Exception;

    void deleteReport(String reportId) throws Exception;

    YouTrackAuthenticationMethod getAuthenticationMethod();
}
