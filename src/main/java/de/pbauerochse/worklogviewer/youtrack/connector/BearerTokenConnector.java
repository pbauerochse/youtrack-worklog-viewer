package de.pbauerochse.worklogviewer.youtrack.connector;

import de.pbauerochse.worklogviewer.youtrack.YouTrackConnector;
import de.pbauerochse.worklogviewer.youtrack.createreport.request.CreateReportRequestEntity;
import de.pbauerochse.worklogviewer.youtrack.createreport.response.ReportDetailsResponse;
import de.pbauerochse.worklogviewer.youtrack.domain.GroupByCategory;
import de.pbauerochse.worklogviewer.youtrack.domain.WorklogReport;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Connector which uses permanent tokens to
 * authenticate with YouTrack
 * <p>
 * The users needs to have "Read Service" role to
 * create a permanent token.
 */
public class BearerTokenConnector implements YouTrackConnector {

    @Override
    public List<GroupByCategory> getPossibleGroupByCategories() throws Exception {
        return null;
    }

    @Override
    public ReportDetailsResponse createReport(CreateReportRequestEntity requestEntity) throws Exception {
        return null;
    }

    @Override
    public ReportDetailsResponse getReportDetails(String reportId) throws Exception {
        return null;
    }

    @Override
    public ByteArrayInputStream downloadReport(String reportId) throws Exception {
        return null;
    }

    @Override
    public void deleteReport(String reportId) throws Exception {

    }

    @Override
    public void fetchTaskDetails(WorklogReport report) throws Exception {

    }

    @Override
    public YouTrackAuthenticationMethod getAuthenticationMethod() {
        return YouTrackAuthenticationMethod.PERMANENT_TOKEN;
    }
}
