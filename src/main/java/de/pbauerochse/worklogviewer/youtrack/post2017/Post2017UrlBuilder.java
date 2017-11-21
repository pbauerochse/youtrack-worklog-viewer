package de.pbauerochse.worklogviewer.youtrack.post2017;

import de.pbauerochse.worklogviewer.util.SettingsUtil;
import de.pbauerochse.worklogviewer.youtrack.YouTrackUrlBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.nio.charset.Charset;
import java.util.List;

class Post2017UrlBuilder implements YouTrackUrlBuilder {

    @Override
    public String getUsernamePasswordLoginUrl() {
        throw new UnsupportedOperationException("Username Password Authentication is not supported with Versions >= 2017.03");
    }

    @Override
    public String getGroupByCriteriaUrl() {
        return buildYoutrackApiUrl("/rest/reports/timeReports/possibleGroupByCategories");
    }

    @Override
    public String getCreateReportUrl() {
        return buildYoutrackApiUrl("/api/reports?fields=$type,authors(fullName,id,login,ringId),effectiveQuery,effectiveQueryUrl,id,invalidationInterval,name,own,owner(id,login),pinned,projects(id,name,shortName),query,sprint(id,name),status(calculationInProgress,error(id),errorMessage,isOutdated,lastCalculated,progress,wikifiedErrorMessage),visibleTo(id,name),wikifiedError,windowSize,workTypes(id,name)");
    }

    @Override
    public String getReportDetailsUrl(String reportId) {
        String template = buildYoutrackApiUrl("/api/reports/%s/status?fields=calculationInProgress,error(id),errorMessage,isOutdated,lastCalculated,progress,wikifiedErrorMessage");
        return String.format(template, reportId);
    }

    @Override
    public String getDownloadReportUrl(String reportId) {
        String template = buildYoutrackApiUrl("/rest/current/reports/%s/export");
        return String.format(template, reportId);
    }

    @Override
    public String getDeleteReportUrl(String reportId) {
        String template = buildYoutrackApiUrl("/api/reports/%s?fields=$type,authors(fullName,id,login,ringId),effectiveQuery,effectiveQueryUrl,id,invalidationInterval,name,own,owner(id,login),pinned,projects(id,name,shortName),query,sprint(id,name),status(calculationInProgress,error(id),errorMessage,isOutdated,lastCalculated,progress,wikifiedErrorMessage),visibleTo(id,name),wikifiedError,windowSize,workTypes(id,name)");
        return String.format(template, reportId);
    }

    @Override
    public String getIssueDetailsUrl(List<NameValuePair> fetchIssuesParameters) {
        String template = buildYoutrackApiUrl("/rest/issue?%s");
        String issuesQuery = URLEncodedUtils.format(fetchIssuesParameters, Charset.forName("utf-8"));
        return String.format(template, issuesQuery);
    }

    private String buildYoutrackApiUrl(String path) {
        SettingsUtil.Settings settings = SettingsUtil.loadSettings();
        StringBuilder finalUrl = new StringBuilder(StringUtils.trim(settings.getYoutrackUrl()));

        if (!StringUtils.endsWith(settings.getYoutrackUrl(), "/") && !StringUtils.startsWith(path, "/")) {
            finalUrl.append('/');
        }

        return finalUrl.append(path).toString();
    }

}
