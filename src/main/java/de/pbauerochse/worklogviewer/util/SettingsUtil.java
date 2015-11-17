package de.pbauerochse.worklogviewer.util;

import de.pbauerochse.worklogviewer.domain.ReportTimerange;
import de.pbauerochse.worklogviewer.youtrack.connector.YouTrackAuthenticationMethod;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.DayOfWeek;
import java.util.Properties;

import static java.time.DayOfWeek.SATURDAY;
import static java.time.DayOfWeek.SUNDAY;

/**
 * @author Patrick Bauerochse
 * @since 01.04.15
 */
public class SettingsUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsUtil.class);

    private static final File CONFIG_FILE_LOCATION = new File(System.getProperty("user.home"), "youtrack-worklog.properties");

    private static final String WINDOW_X_PROPERTY = "window.x";
    private static final String WINDOW_Y_PROPERTY = "window.y";
    private static final String WINDOW_WIDTH_PROPERTY = "window.width";
    private static final String WINDOW_HEIGHT_PROPERTY = "window.height";
    private static final String WORK_HOURS_PROPERTY = "workhours";

    private static final String YOUTRACK_AUTHENITCATION_METHOD_PROPERTY = "auth_method";
    private static final String YOUTRACK_USERNAME_PROPERTY = "username";
    private static final String YOUTRACK_PASSWORD_PROPERTY = "password";
    private static final String YOUTRACK_URL_PROPERTY = "youtrackurl";
    private static final String YOUTRACK_OAUTH_SERVICE_ID_PROPERTY = "oauth_service_id";
    private static final String YOUTRACK_OAUTH_SERVICE_SECRET = "oauth_service_secret";
    private static final String YOUTRACK_OAUTH_HUB_URL = "oauth_hub_url";

    private static final String SHOW_ALL_WORKLOGS_PROPERTY = "showonlyowntimelogs.enabled";
    private static final String SHOW_STATISTICS_PROPERTY = "statistics.enabled";
    private static final String AUTOLOAD_DATA_PROPERTY = "autoload.enabled";
    private static final String AUTOLOAD_DATA_TIMERANGE_PROPERTY = "autoload.timerange";
    private static final String SHOW_DECIMAL_HOURS_IN_EXCEL_REPORT = "excel.decimaltimes";
    private static final String COLLAPSE_STATE_PROPERTY = "collapse.state";
    private static final String HIGHLIGHT_STATE_PROPERTY = "highlight.state";

    private static Settings settings;

    /**
     * Loads the user settings for the config file if present
     * if not it returns the default settings
     *
     * @return the loaded Settings object
     */
    public static Settings loadSettings() {
        if (settings == null) {
            settings = new Settings();

            if (CONFIG_FILE_LOCATION.exists()) {
                LOGGER.debug("Loading configuration from {}", CONFIG_FILE_LOCATION.getAbsolutePath());
                Properties properties = new Properties();
                try {
                    properties.load(new FileInputStream(CONFIG_FILE_LOCATION));
                    applyFromPropertiesToSettings(settings, properties);
                } catch (IOException e) {
                    LOGGER.error("Could not read settings from {}", CONFIG_FILE_LOCATION.getAbsolutePath(), e);
                    throw ExceptionUtil.getRuntimeException("exceptions.settings.read", e, CONFIG_FILE_LOCATION.getAbsolutePath());
                }
            }
        }

        return settings;
    }

    /**
     * Saves the current Settings properties to the
     * config file
     */
    public static void saveSettings() {
        if (!CONFIG_FILE_LOCATION.exists()) {
            try {
                LOGGER.debug("Trying to create new settings file at {}", CONFIG_FILE_LOCATION.getAbsolutePath());
                CONFIG_FILE_LOCATION.createNewFile();
            } catch (IOException e) {
                LOGGER.error("Could not create settings file at {}", CONFIG_FILE_LOCATION.getAbsolutePath(), e);
                throw ExceptionUtil.getRuntimeException("exceptions.settings.create", e, CONFIG_FILE_LOCATION.getAbsolutePath());
            }
        }

        Properties properties = getAsProperties(settings);
        try {
            LOGGER.debug("Saving properties to settings file");
            properties.store(new FileOutputStream(CONFIG_FILE_LOCATION), "Settings file for YouTrack worklog viewer");
        } catch (IOException e) {
            LOGGER.error("Could not save settings to {}", CONFIG_FILE_LOCATION.getAbsolutePath(), e);
            throw ExceptionUtil.getRuntimeException("exceptions.settings.write", e, CONFIG_FILE_LOCATION.getAbsolutePath());
        }
    }

    private static void applyFromPropertiesToSettings(Settings settings, Properties properties) {
        String windowXAsString = properties.getProperty(WINDOW_X_PROPERTY);
        if (StringUtils.isNotBlank(windowXAsString)) {
            try {
                settings.setWindowX(Integer.parseInt(windowXAsString));
            } catch (NumberFormatException e) {
                // ignore
                LOGGER.warn("Could not convert {} to Integer for setting {}", windowXAsString, WINDOW_X_PROPERTY);
            }
        }

        String windowYAsString = properties.getProperty(WINDOW_Y_PROPERTY);
        if (StringUtils.isNotBlank(windowYAsString)) {
            try {
                settings.setWindowY(Integer.parseInt(windowYAsString));
            } catch (NumberFormatException e) {
                // ignore
                LOGGER.warn("Could not convert {} to Integer for setting {}", windowYAsString, WINDOW_Y_PROPERTY);
            }
        }

        String windowWidthAsString = properties.getProperty(WINDOW_WIDTH_PROPERTY);
        if (StringUtils.isNotBlank(windowWidthAsString)) {
            try {
                settings.setWindowWidth(Integer.parseInt(windowWidthAsString));
            } catch (NumberFormatException e) {
                // ignore
                LOGGER.warn("Could not convert {} to Integer for setting {}", windowWidthAsString, WINDOW_WIDTH_PROPERTY);
            }
        }

        String windowHeightAsString = properties.getProperty(WINDOW_HEIGHT_PROPERTY);
        if (StringUtils.isNotBlank(windowHeightAsString)) {
            try {
                settings.setWindowHeight(Integer.parseInt(windowHeightAsString));
            } catch (NumberFormatException e) {
                // ignore
                LOGGER.warn("Could not convert {} to Integer for setting {}", windowHeightAsString, WINDOW_HEIGHT_PROPERTY);
            }
        }

        String workHoursAsString = properties.getProperty(WORK_HOURS_PROPERTY);
        if (StringUtils.isNotBlank(workHoursAsString)) {
            try {
                settings.setWorkHoursADay(Integer.parseInt(workHoursAsString));
            } catch (NumberFormatException e) {
                // ignore
                LOGGER.warn("Could not convert {} to Integer for setting {}", workHoursAsString, WORK_HOURS_PROPERTY);
            }
        }

        // youtrack authentication
        String youtrackAuthenticationMethodAsString = properties.getProperty(YOUTRACK_AUTHENITCATION_METHOD_PROPERTY);
        if (StringUtils.isNotBlank(youtrackAuthenticationMethodAsString)) {
            try {
                YouTrackAuthenticationMethod method = YouTrackAuthenticationMethod.valueOf(youtrackAuthenticationMethodAsString);
                settings.setYouTrackAuthenticationMethod(method);
            } catch (IllegalArgumentException e) {
                LOGGER.warn("Could not determine AuthenticationMethod by settings value {}", youtrackAuthenticationMethodAsString);
            }
        }

        settings.setYoutrackUrl(properties.getProperty(YOUTRACK_URL_PROPERTY));
        settings.setYoutrackUsername(properties.getProperty(YOUTRACK_USERNAME_PROPERTY));
        settings.setYoutrackOAuthServiceId(properties.getProperty(YOUTRACK_OAUTH_SERVICE_ID_PROPERTY));
        settings.setYoutrackOAuthHubUrl(properties.getProperty(YOUTRACK_OAUTH_HUB_URL));
        String encryptedUserPassword = properties.getProperty(YOUTRACK_PASSWORD_PROPERTY);
        if (StringUtils.isNotBlank(encryptedUserPassword)) {
            try {
                settings.setYoutrackPassword(EncryptionUtil.decryptEncryptedString(encryptedUserPassword));
            } catch (GeneralSecurityException e) {
                LOGGER.error("Could not decrypt password from settings file", e);
                throw ExceptionUtil.getIllegalStateException("exceptions.settings.password.decrypt", e);
            }
        }

        String encryptedOAuthServiceSecret = properties.getProperty(YOUTRACK_OAUTH_SERVICE_SECRET);
        if (StringUtils.isNotBlank(encryptedOAuthServiceSecret)) {
            try {
                settings.setYoutrackOAuthServiceSecret(EncryptionUtil.decryptEncryptedString(encryptedOAuthServiceSecret));
            } catch (GeneralSecurityException e) {
                LOGGER.error("Could not decrypt oauth secret from settings file", e);
                throw ExceptionUtil.getIllegalStateException("exceptions.settings.oauthsecret.decrypt", e);
            }
        }

        String showOnlyOwnWorklogsAsString = properties.getProperty(SHOW_ALL_WORKLOGS_PROPERTY);
        if (StringUtils.isNotBlank(showOnlyOwnWorklogsAsString)) {
            settings.setShowAllWorklogs(Boolean.valueOf(showOnlyOwnWorklogsAsString));
        }

        String showStatisticsAsString = properties.getProperty(SHOW_STATISTICS_PROPERTY);
        if (StringUtils.isNotBlank(showStatisticsAsString)) {
            settings.setShowStatistics(Boolean.valueOf(showStatisticsAsString));
        }

        String autoloadDataAsString = properties.getProperty(AUTOLOAD_DATA_PROPERTY);
        if (StringUtils.isNotBlank(autoloadDataAsString)) {
            settings.setLoadDataAtStartup(Boolean.valueOf(autoloadDataAsString));
        }

        String showDecimalHoursInExcel = properties.getProperty(SHOW_DECIMAL_HOURS_IN_EXCEL_REPORT);
        if (StringUtils.isNotBlank(showDecimalHoursInExcel)) {
            settings.setShowDecimalHourTimesInExcelReport(Boolean.valueOf(showDecimalHoursInExcel));
        }

        String autoloadDataTimerangeAsString = properties.getProperty(AUTOLOAD_DATA_TIMERANGE_PROPERTY);
        if (StringUtils.isNotBlank(autoloadDataTimerangeAsString)) {
            try {
                ReportTimerange reportTimerange = ReportTimerange.valueOf(autoloadDataTimerangeAsString);
                settings.setLastUsedReportTimerange(reportTimerange);
            } catch (IllegalArgumentException e) {
                LOGGER.warn("Could not determine ReportTimerange by settings value {}", autoloadDataTimerangeAsString);
            }
        }

        String collapseStateAsString = properties.getProperty(COLLAPSE_STATE_PROPERTY);
        if (StringUtils.isNotBlank(collapseStateAsString)) {
            try {
                settings.setCollapseState(Integer.parseInt(collapseStateAsString));
            } catch (NumberFormatException e) {
                LOGGER.warn("Could not get collapse state from {}", collapseStateAsString);
            }
        }

        String highlightStateAsString = properties.getProperty(HIGHLIGHT_STATE_PROPERTY);
        if (StringUtils.isNotBlank(highlightStateAsString)) {
            try {
                settings.setHighlightState(Integer.parseInt(highlightStateAsString));
            } catch (NumberFormatException e) {
                LOGGER.warn("Could not get highlight state from {}", highlightStateAsString);
            }
        }
    }

    private static Properties getAsProperties(Settings settings) {
        Properties properties = new Properties();

        properties.setProperty(WINDOW_X_PROPERTY, String.valueOf(settings.getWindowX()));
        properties.setProperty(WINDOW_Y_PROPERTY, String.valueOf(settings.getWindowY()));
        properties.setProperty(WINDOW_WIDTH_PROPERTY, String.valueOf(settings.getWindowWidth()));
        properties.setProperty(WINDOW_HEIGHT_PROPERTY, String.valueOf(settings.getWindowHeight()));
        properties.setProperty(WORK_HOURS_PROPERTY, String.valueOf(settings.getWorkHoursADay()));
        properties.setProperty(SHOW_ALL_WORKLOGS_PROPERTY, String.valueOf(settings.isShowAllWorklogs()));
        properties.setProperty(SHOW_STATISTICS_PROPERTY, String.valueOf(settings.isShowStatistics()));
        properties.setProperty(AUTOLOAD_DATA_PROPERTY, String.valueOf(settings.isLoadDataAtStartup()));
        properties.setProperty(SHOW_DECIMAL_HOURS_IN_EXCEL_REPORT, String.valueOf(settings.isShowDecimalHourTimesInExcelReport()));

        if (StringUtils.isNotBlank(settings.getYoutrackUrl())) {
            properties.setProperty(YOUTRACK_URL_PROPERTY, settings.getYoutrackUrl());
        }

        if (StringUtils.isNotBlank(settings.getYoutrackOAuthHubUrl())) {
            properties.setProperty(YOUTRACK_OAUTH_HUB_URL, settings.getYoutrackOAuthHubUrl());
        }

        if (StringUtils.isNotBlank(settings.getYoutrackUsername())) {
            properties.setProperty(YOUTRACK_USERNAME_PROPERTY, settings.getYoutrackUsername());
        }

        properties.setProperty(YOUTRACK_AUTHENITCATION_METHOD_PROPERTY, settings.getYouTrackAuthenticationMethod().name());

        if (StringUtils.isNotBlank(settings.getYoutrackOAuthServiceId())) {
            properties.setProperty(YOUTRACK_OAUTH_SERVICE_ID_PROPERTY, settings.getYoutrackOAuthServiceId());
        }

        if (StringUtils.isNotBlank(settings.getYoutrackOAuthServiceSecret())) {
            try {
                properties.setProperty(YOUTRACK_OAUTH_SERVICE_SECRET, EncryptionUtil.encryptCleartextString(settings.getYoutrackOAuthServiceSecret()));
            } catch (GeneralSecurityException e) {
                LOGGER.error("Could not encrypt oauth service secret for settings file", e);
                throw ExceptionUtil.getIllegalStateException("exceptions.settings.oauthsecret.encrypt", e);
            }
        }

        if (StringUtils.isNotBlank(settings.getYoutrackPassword())) {
            try {
                properties.setProperty(YOUTRACK_PASSWORD_PROPERTY, EncryptionUtil.encryptCleartextString(settings.getYoutrackPassword()));
            } catch (GeneralSecurityException e) {
                LOGGER.error("Could not encrypt password for settings file", e);
                throw ExceptionUtil.getIllegalStateException("exceptions.settings.password.encrypt", e);
            }
        }

        if (settings.getLastUsedReportTimerange() != null) {
            properties.setProperty(AUTOLOAD_DATA_TIMERANGE_PROPERTY, settings.getLastUsedReportTimerange().name());
        }

        properties.setProperty(COLLAPSE_STATE_PROPERTY, String.valueOf(settings.collapseState));
        properties.setProperty(HIGHLIGHT_STATE_PROPERTY, String.valueOf(settings.highlightState));

        return properties;
    }

    public static class Settings {

        private int windowWidth = 800;

        private int windowHeight = 600;

        private int windowX = 0;

        private int windowY = 0;

        private int workHoursADay = 8;

        private YouTrackAuthenticationMethod youTrackAuthenticationMethod = YouTrackAuthenticationMethod.HTTP_API;

        private String youtrackOAuthHubUrl;

        private String youtrackOAuthServiceId;

        private String youtrackOAuthServiceSecret;

        private String youtrackUrl;

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

        public void setCollapseState(int collapseState) {
            this.collapseState = collapseState;
        }

        public int getHighlightState() {
            return highlightState;
        }

        public void setHighlightState(int highlightState) {
            this.highlightState = highlightState;
        }

        public boolean hasMissingConnectionParameters() {
            return StringUtils.isEmpty(youtrackUrl) ||
                   StringUtils.isEmpty(youtrackUsername) ||
                   StringUtils.isEmpty(youtrackPassword) ||
                    (
                        youTrackAuthenticationMethod == YouTrackAuthenticationMethod.OAUTH2 &&
                        (StringUtils.isEmpty(youtrackOAuthHubUrl) || StringUtils.isEmpty(youtrackOAuthServiceId) || StringUtils.isEmpty(youtrackOAuthServiceSecret))
                    );
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

        public int createBitMaskState(DayOfWeek... setDays) {
            int bitmask = 0;

            for (DayOfWeek day : setDays) {
                bitmask = setBitValue(bitmask,day, true);
            }

            return bitmask;
        }

        public int setBitValue(int state, DayOfWeek day, boolean selected) {
            if (selected) {
                return state | (1 << day.ordinal());
            } else {
                return state & ~(1 << day.ordinal());
            }
        }

        public boolean hasBitValue(int state, DayOfWeek day) {
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
            return result;
        }

        private int getHashOrZero(Object o) {
            if (o == null) return 0;
            return o.hashCode();
        }
    }

}
