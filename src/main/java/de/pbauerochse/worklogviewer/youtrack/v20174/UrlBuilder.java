package de.pbauerochse.worklogviewer.youtrack.v20174;

import de.pbauerochse.worklogviewer.settings.Settings;
import de.pbauerochse.worklogviewer.settings.SettingsUtil;
import de.pbauerochse.worklogviewer.youtrack.YouTrackUrlBuilder;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.nio.charset.Charset;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.*;

class UrlBuilder implements YouTrackUrlBuilder {

    @Override
    public String getUsernamePasswordLoginUrl() {
        throw new UnsupportedOperationException("Username Password Authentication is not supported with Versions >= 2017.03");
    }

    @Override
    public String getGroupByCriteriaUrl() {
        return buildYoutrackApiUrl("/api/filterFields?fieldTypes=version%5B1%5D&fieldTypes=ownedField%5B1%5D&fieldTypes=state%5B1%5D&fieldTypes=user%5B1%5D&fieldTypes=enum%5B1%5D&fieldTypes=date&fieldTypes=integer&fieldTypes=float&fieldTypes=period&fieldTypes=project&fields=id,$type,presentation,name,aggregateable,sortable,customField(id,fieldType(id),name,localizedName),projects(id,name)&includeNonFilterFields=true");
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
        String template = buildYoutrackApiUrl("/api/reports/%s/export");
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
        Settings settings = SettingsUtil.getSettings();
        return removeEnd(trim(settings.getYoutrackUrl()), "/") + "/" + removeStart(trim(path), "/");
    }

}
