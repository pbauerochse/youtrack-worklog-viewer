package de.pbauerochse.worklogviewer.settings;

import de.pbauerochse.worklogviewer.domain.ReportTimerange;
import de.pbauerochse.worklogviewer.util.EncryptionUtil;
import de.pbauerochse.worklogviewer.util.ExceptionUtil;
import de.pbauerochse.worklogviewer.youtrack.YouTrackAuthenticationMethod;
import de.pbauerochse.worklogviewer.youtrack.YouTrackVersion;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Properties;

/**
 * Loads and stores the Settings object
 * in a properties file.
 *
 * Can also be used, to obtain the singleton instance
 * of the Settings or SettingsViewModel instance
 */
public class SettingsUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsUtil.class);

    private static final File CONFIG_FILE_LOCATION = new File(System.getProperty("user.home"), "youtrack-worklog.properties");

    private static final String WINDOW_X_PROPERTY = "window.x";
    private static final String WINDOW_Y_PROPERTY = "window.y";
    private static final String WINDOW_WIDTH_PROPERTY = "window.width";
    private static final String WINDOW_HEIGHT_PROPERTY = "window.height";
    private static final String WORK_HOURS_PROPERTY = "workhours";

    private static final String YOUTRACK_VERSION_PROPERTY = "youtrackversion";
    private static final String YOUTRACK_AUTHENITCATION_METHOD_PROPERTY = "auth_method";
    private static final String YOUTRACK_USERNAME_PROPERTY = "username";
    private static final String YOUTRACK_PASSWORD_PROPERTY = "password";
    private static final String YOUTRACK_URL_PROPERTY = "youtrackurl";
    private static final String YOUTRACK_OAUTH_SERVICE_ID_PROPERTY = "oauth_service_id";
    private static final String YOUTRACK_OAUTH_SERVICE_SECRET = "oauth_service_secret";
    private static final String YOUTRACK_OAUTH_HUB_URL = "oauth_hub_url";
    private static final String YOUTRACK_PERMANENT_TOKEN = "permanent_token";

    private static final String SHOW_ALL_WORKLOGS_PROPERTY = "showonlyowntimelogs.enabled";
    private static final String SHOW_STATISTICS_PROPERTY = "statistics.enabled";
    private static final String AUTOLOAD_DATA_PROPERTY = "autoload.enabled";
    private static final String AUTOLOAD_DATA_TIMERANGE_PROPERTY = "autoload.timerange";
    private static final String SHOW_DECIMAL_HOURS_IN_EXCEL_REPORT = "excel.decimaltimes";
    private static final String COLLAPSE_STATE_PROPERTY = "collapse.state";
    private static final String HIGHLIGHT_STATE_PROPERTY = "highlight.state";

    private static Settings settings;
    private static SettingsViewModel settingsViewModel;

    /**
     * Loads the user settings for the config file if present
     * if not it returns the default settings
     *
     * @return the loaded Settings object
     */
    public static Settings getSettings() {
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

    public static SettingsViewModel getSettingsViewModel() {
        if (settingsViewModel == null) {
            settingsViewModel = new SettingsViewModel();
        }
        return settingsViewModel;
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

        // youtrack version
        String youtrackVersionAsString = properties.getProperty(YOUTRACK_VERSION_PROPERTY);
        if (StringUtils.isNotBlank(youtrackVersionAsString)) {
            try {
                YouTrackVersion version = YouTrackVersion.valueOf(youtrackVersionAsString);
                settings.setYouTrackVersion(version);
            } catch (IllegalArgumentException e) {
                LOGGER.warn("Could not determine YouTrackVersion by settings value {}", youtrackVersionAsString);
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

        String encryptedPermanentToken = properties.getProperty(YOUTRACK_PERMANENT_TOKEN);
        if (StringUtils.isNotBlank(encryptedPermanentToken)) {
            try {
                settings.setYoutrackPermanentToken(EncryptionUtil.decryptEncryptedString(encryptedPermanentToken));
            } catch (GeneralSecurityException e) {
                LOGGER.error("Could not decrypt permanent token from settings file", e);
                throw ExceptionUtil.getIllegalStateException("exceptions.settings.permanenttoken.decrypt", e);
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
        properties.setProperty(YOUTRACK_VERSION_PROPERTY, settings.getYouTrackVersion().name());

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

        if (StringUtils.isNotBlank(settings.getYoutrackPermanentToken())) {
            try {
                properties.setProperty(YOUTRACK_PERMANENT_TOKEN, EncryptionUtil.encryptCleartextString(settings.getYoutrackPermanentToken()));
            } catch (GeneralSecurityException e) {
                LOGGER.error("Could not encrypt permanent token for settings file", e);
                throw ExceptionUtil.getIllegalStateException("exceptions.settings.permanenttoken.encrypt", e);
            }
        }

        if (settings.getLastUsedReportTimerange() != null) {
            properties.setProperty(AUTOLOAD_DATA_TIMERANGE_PROPERTY, settings.getLastUsedReportTimerange().name());
        }

        properties.setProperty(COLLAPSE_STATE_PROPERTY, String.valueOf(settings.getCollapseState()));
        properties.setProperty(HIGHLIGHT_STATE_PROPERTY, String.valueOf(settings.getHighlightState()));

        return properties;
    }

}
