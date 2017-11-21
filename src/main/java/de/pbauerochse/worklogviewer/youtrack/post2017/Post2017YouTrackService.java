package de.pbauerochse.worklogviewer.youtrack.post2017;

import com.google.common.collect.ImmutableList;
import de.pbauerochse.worklogviewer.youtrack.*;
import de.pbauerochse.worklogviewer.youtrack.domain.GroupByCategory;
import de.pbauerochse.worklogviewer.youtrack.domain.WorklogReport;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

public class Post2017YouTrackService implements YouTrackService {

    private static final YouTrackUrlBuilder URL_BUILDER = new Post2017UrlBuilder();

    @Override
    public List<GroupByCategory> getPossibleGroupByCategories() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public ReportDetails createReport(TimereportContext timereportContext) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public ReportDetails getReportDetails(String reportId) throws IOException {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public ByteArrayInputStream downloadReport(String reportId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void deleteReport(String reportId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void fetchTaskDetails(WorklogReport report) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public YouTrackVersion getVersion() {
        return YouTrackVersion.POST_2017;
    }

    @Override
    public List<YouTrackAuthenticationMethod> getValidAuthenticationMethods() {
        return ImmutableList.of(YouTrackAuthenticationMethod.PERMANENT_TOKEN);
    }
}
