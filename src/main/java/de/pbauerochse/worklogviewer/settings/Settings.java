package de.pbauerochse.worklogviewer.settings;

import de.pbauerochse.worklogviewer.domain.ReportTimerange;
import de.pbauerochse.worklogviewer.youtrack.YouTrackAuthenticationMethod;
import de.pbauerochse.worklogviewer.youtrack.YouTrackVersion;

import java.time.DayOfWeek;

import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Settings, that get stored in a properties file
 */
public class Settings {

    private int windowWidth = 800;
    private int windowHeight = 600;
    private int windowX = 0;
    private int windowY = 0;

    private int workHoursADay = 8;

    private YouTrackVersion youTrackVersion;
    private YouTrackAuthenticationMethod youTrackAuthenticationMethod;

    private String youtrackUrl;
    private String youtrackOAuthHubUrl;
    private String youtrackOAuthServiceId;
    private String youtrackOAuthServiceSecret;

    private String youtrackPermanentToken;

    private String youtrackUsername;
    private String youtrackPassword;

    private boolean loadDataAtStartup = false;

    private ReportTimerange lastUsedReportTimerange = ReportTimerange.THIS_WEEK;

    private boolean showStatistics = true;

    private boolean showAllWorklogs = true;

    private boolean showDecimalHourTimesInExcelReport = false;

    private int collapseState = createBitMaskState(SATURDAY, SUNDAY);

    private int highlightState = createBitMaskState(SATURDAY, SUNDAY);

    public int getWindowWidth() {
        return windowWidth;
    }

    public void setWindowWidth(int windowWidth) {
        this.windowWidth = windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public void setWindowHeight(int windowHeight) {
        this.windowHeight = windowHeight;
    }

    public int getWindowX() {
        return windowX;
    }

    public void setWindowX(int windowX) {
        this.windowX = windowX;
    }

    public int getWindowY() {
        return windowY;
    }

    public void setWindowY(int windowY) {
        this.windowY = windowY;
    }

    public int getWorkHoursADay() {
        return workHoursADay;
    }

    public void setWorkHoursADay(int workHoursADay) {
        this.workHoursADay = workHoursADay;
    }

    public YouTrackAuthenticationMethod getYouTrackAuthenticationMethod() {
        return youTrackAuthenticationMethod;
    }

    public void setYouTrackAuthenticationMethod(YouTrackAuthenticationMethod youTrackAuthenticationMethod) {
        this.youTrackAuthenticationMethod = youTrackAuthenticationMethod;
    }

    public YouTrackVersion getYouTrackVersion() {
        return youTrackVersion;
    }

    public void setYouTrackVersion(YouTrackVersion youTrackVersion) {
        this.youTrackVersion = youTrackVersion;
    }

    public String getYoutrackOAuthHubUrl() {
        return youtrackOAuthHubUrl;
    }

    public void setYoutrackOAuthHubUrl(String youtrackOAuthHubUrl) {
        this.youtrackOAuthHubUrl = youtrackOAuthHubUrl;
    }

    public String getYoutrackOAuthServiceId() {
        return youtrackOAuthServiceId;
    }

    public void setYoutrackOAuthServiceId(String youtrackOAuthServiceId) {
        this.youtrackOAuthServiceId = youtrackOAuthServiceId;
    }

    public String getYoutrackOAuthServiceSecret() {
        return youtrackOAuthServiceSecret;
    }

    public void setYoutrackOAuthServiceSecret(String youtrackOAuthServiceSecret) {
        this.youtrackOAuthServiceSecret = youtrackOAuthServiceSecret;
    }

    public String getYoutrackPermanentToken() {
        return youtrackPermanentToken;
    }

    public void setYoutrackPermanentToken(String youtrackPermanentToken) {
        this.youtrackPermanentToken = youtrackPermanentToken;
    }

    public String getYoutrackUrl() {
        return youtrackUrl;
    }

    public void setYoutrackUrl(String youtrackUrl) {
        this.youtrackUrl = youtrackUrl;
    }

    public String getYoutrackUsername() {
        return youtrackUsername;
    }

    public void setYoutrackUsername(String youtrackUsername) {
        this.youtrackUsername = youtrackUsername;
    }

    public String getYoutrackPassword() {
        return youtrackPassword;
    }

    public void setYoutrackPassword(String youtrackPassword) {
        this.youtrackPassword = youtrackPassword;
    }

    public boolean isLoadDataAtStartup() {
        return loadDataAtStartup;
    }

    public void setLoadDataAtStartup(boolean loadDataAtStartup) {
        this.loadDataAtStartup = loadDataAtStartup;
    }

    public ReportTimerange getLastUsedReportTimerange() {
        return lastUsedReportTimerange;
    }

    public void setLastUsedReportTimerange(ReportTimerange lastUsedReportTimerange) {
        this.lastUsedReportTimerange = lastUsedReportTimerange;
    }

    public boolean isShowStatistics() {
        return showStatistics;
    }

    public void setShowStatistics(boolean showStatistics) {
        this.showStatistics = showStatistics;
    }

    public boolean isShowAllWorklogs() {
        return showAllWorklogs;
    }

    public void setShowAllWorklogs(boolean showAllWorklogs) {
        this.showAllWorklogs = showAllWorklogs;
    }

    public boolean isShowDecimalHourTimesInExcelReport() {
        return showDecimalHourTimesInExcelReport;
    }

    public void setShowDecimalHourTimesInExcelReport(boolean showDecimalHourTimesInExcelReport) {
        this.showDecimalHourTimesInExcelReport = showDecimalHourTimesInExcelReport;
    }

    public int getCollapseState() {
        return collapseState;
    }

    void setCollapseState(int collapseState) {
        this.collapseState = collapseState;
    }

    public int getHighlightState() {
        return highlightState;
    }

    void setHighlightState(int highlightState) {
        this.highlightState = highlightState;
    }

    public boolean hasMissingConnectionParameters() {
        return isEmpty(youtrackUrl) || hasMissingPropertiesForSelectedAuthenticationMethod();
    }

    private boolean hasMissingPropertiesForSelectedAuthenticationMethod() {
        if (youTrackAuthenticationMethod == null || youTrackVersion == null) {
            return true;
        }

        switch (youTrackAuthenticationMethod) {
            case OAUTH2:
                return hasMissingOAuthSettings();

            case PERMANENT_TOKEN:
                return hasMissingPermanentTokenSettings();

            case HTTP_API:
            default:
                return hasMissingUsernamePasswordSettings();
        }
    }

    private boolean hasMissingUsernamePasswordSettings() {
        return isEmpty(youtrackUsername) || isEmpty(youtrackPassword);
    }

    private boolean hasMissingOAuthSettings() {
        return hasMissingUsernamePasswordSettings() ||
                isEmpty(youtrackOAuthHubUrl) || isEmpty(youtrackOAuthServiceId) || isEmpty(youtrackOAuthServiceSecret);
    }

    private boolean hasMissingPermanentTokenSettings() {
        return isEmpty(youtrackPermanentToken);
    }


    public boolean hasHighlightState(DayOfWeek day) {
        return hasBitValue(highlightState, day);
    }

    public boolean hasCollapseState(DayOfWeek day) {
        return hasBitValue(collapseState, day);
    }

    public void setHighlightState(DayOfWeek day, boolean selected) {
        highlightState = setBitValue(highlightState, day, selected);
    }

    public void setCollapseState(DayOfWeek day, boolean selected) {
        collapseState = setBitValue(collapseState, day, selected);
    }

    private int createBitMaskState(DayOfWeek... setDays) {
        int bitmask = 0;

        for (DayOfWeek day : setDays) {
            bitmask = setBitValue(bitmask, day, true);
        }

        return bitmask;
    }

    private int setBitValue(int state, DayOfWeek day, boolean selected) {
        if (selected) {
            return state | (1 << day.ordinal());
        } else {
            return state & ~(1 << day.ordinal());
        }
    }

    private boolean hasBitValue(int state, DayOfWeek day) {
        int bitValue = (1 << day.ordinal());
        return (state & bitValue) == bitValue;
    }

    public int getConnectionParametersHashCode() {
        int result = getHashOrZero(youtrackUrl);
        result = 31 * result + getHashOrZero(youtrackUsername);
        result = 31 * result + getHashOrZero(youtrackPassword);
        result = 31 * result + getHashOrZero(youTrackAuthenticationMethod);
        result = 31 * result + getHashOrZero(youtrackOAuthServiceId);
        result = 31 * result + getHashOrZero(youtrackOAuthServiceSecret);
        result = 31 * result + getHashOrZero(youtrackOAuthHubUrl);
        result = 31 * result + getHashOrZero(youtrackPermanentToken);
        return result;
    }

    private int getHashOrZero(Object o) {
        if (o == null) return 0;
        return o.hashCode();
    }
}
