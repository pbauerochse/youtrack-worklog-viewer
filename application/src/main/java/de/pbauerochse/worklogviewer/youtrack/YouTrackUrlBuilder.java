package de.pbauerochse.worklogviewer.youtrack;

import org.apache.http.NameValuePair;

import java.util.List;

@Deprecated
public interface YouTrackUrlBuilder {

    String getGroupByCriteriaUrl();

    String getCreateReportUrl();

    String getReportDetailsUrl(String reportId);

    String getDownloadReportUrl(String reportId);

    String getDeleteReportUrl(String reportId);

    String getIssueDetailsUrl(List<NameValuePair> fetchIssuesParameters);

}
