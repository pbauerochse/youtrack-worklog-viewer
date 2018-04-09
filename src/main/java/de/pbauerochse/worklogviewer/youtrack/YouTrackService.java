package de.pbauerochse.worklogviewer.youtrack;

import de.pbauerochse.worklogviewer.youtrack.domain.GroupByCategory;
import de.pbauerochse.worklogviewer.youtrack.domain.WorklogReport;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Allows the interaction with the YouTrack API
 */
public interface YouTrackService {

    /**
     * Returns all available GroupByCategories
     */
    List<GroupByCategory> getPossibleGroupByCategories();

    ReportDetails createReport(TimereportContext timereportContext);

    ReportDetails getReportDetails(String reportId) throws IOException;

    ByteArrayInputStream downloadReport(String reportId);

    void deleteReport(String reportId);

    void fetchTaskDetails(WorklogReport report);

    List<YouTrackVersion> getSupportedVersions();

    List<YouTrackAuthenticationMethod> getSupportedAuthenticationMethods();

}
