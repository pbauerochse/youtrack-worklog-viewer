package de.pbauerochse.worklogviewer.youtrack.v20173;

import de.pbauerochse.worklogviewer.settings.Settings;
import de.pbauerochse.worklogviewer.settings.SettingsUtil;
import de.pbauerochse.worklogviewer.youtrack.YouTrackUrlBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.nio.charset.Charset;
import java.util.List;

class UrlBuilder implements YouTrackUrlBuilder {

    @Override
    public String getUsernamePasswordLoginUrl() {
        return buildYoutrackApiUrl("user/login");
    }

    @Override
    public String getGroupByCriteriaUrl() {
        return buildYoutrackApiUrl("reports/timeReports/possibleGroupByCategories");
    }

    @Override
    public String getCreateReportUrl() {
        return buildYoutrackApiUrl("current/reports");
    }

    @Override
    public String getReportDetailsUrl(String reportId) {
        String template = buildYoutrackApiUrl("current/reports/%s");
        return String.format(template, reportId);
    }

    @Override
    public String getDownloadReportUrl(String reportId) {
        String template = buildYoutrackApiUrl("current/reports/%s/export");
        return String.format(template, reportId);
    }

    @Override
    public String getDeleteReportUrl(String reportId) {
        String template = buildYoutrackApiUrl("current/reports/%s");
        return String.format(template, reportId);
    }

    @Override
    public String getIssueDetailsUrl(List<NameValuePair> fetchIssuesParameters) {
        String template = buildYoutrackApiUrl("issue?%s");
        String issuesQuery = URLEncodedUtils.format(fetchIssuesParameters, Charset.forName("utf-8"));
        return String.format(template, issuesQuery);
    }

    private String buildYoutrackApiUrl(String path) {
        Settings settings = SettingsUtil.getSettings();
        StringBuilder finalUrl = new StringBuilder(StringUtils.trim(settings.getYoutrackUrl()));

        if (!StringUtils.endsWith(settings.getYoutrackUrl(), "/") && !StringUtils.startsWith(path, "/")) {
            finalUrl.append('/');
        }

        if (!StringUtils.endsWith(finalUrl, "rest/")) {
            finalUrl.append("rest/");
        }

        return finalUrl.append(path).toString();
    }

}
