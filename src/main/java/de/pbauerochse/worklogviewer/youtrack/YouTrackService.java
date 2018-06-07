package de.pbauerochse.worklogviewer.youtrack;

import de.pbauerochse.worklogviewer.youtrack.domain.GroupByCategory;

import java.util.List;

/**
 * Allows the interaction with the YouTrack API
 */
public interface YouTrackService {

    /**
     * Returns all available GroupByCategories
     */
    List<GroupByCategory> getPossibleGroupByCategories();

    /**
     * Loads the TimeReport for the given parameters
     */
    TimeReport getReport(TimeReportParameters parameters, ProgressCallback progressCallback);

    /**
     * Returns the YouTrack versions this
     * Service can retrieve TimeReports from
     */
    List<YouTrackVersion> getSupportedVersions();

//    ReportDetails createReport(TimeReportParameters timereportContext);
//    ReportDetails getReportDetails(String reportId) throws IOException;
//    ByteArrayInputStream downloadReport(String reportId);
//    void deleteReport(String reportId);
//    void fetchTaskDetails(WorklogReport report);

}
