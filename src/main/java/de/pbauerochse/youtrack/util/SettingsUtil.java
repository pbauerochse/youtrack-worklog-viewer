package de.pbauerochse.youtrack.util;

import de.pbauerochse.youtrack.domain.ReportTimerange;
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
    private static final String YOUTRACK_USERNAME_PROPERTY = "username";
    private static final String YOUTRACK_PASSWORD_PROPERTY = "password";
    private static final String YOUTRACK_URL_PROPERTY = "youtrackurl";
    private static final String WORK_HOURS_PROPERTY = "workhours";

    private static final String SHOW_ALL_WORKLOGS_PROPERTY = "showonlyowntimelogs.enabled";
    private static final String SHOW_STATISTICS_PROPERTY = "statistics.enabled";
    private static final String AUTOLOAD_DATA_PROPERTY = "autoload.enabled";
    private static final String AUTOLOAD_DATA_TIMERANGE_PROPERTY = "autoload.timerange";

    private static Settings settings;

    /**
     * Loads the user settings for the config file if present
     * if not it returns the default settings
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

        settings.setYoutrackUrl(properties.getProperty(YOUTRACK_URL_PROPERTY));
        settings.setYoutrackUsername(properties.getProperty(YOUTRACK_USERNAME_PROPERTY));
        String encryptedPassword = properties.getProperty(YOUTRACK_PASSWORD_PROPERTY);
        if (StringUtils.isNotBlank(encryptedPassword)) {
            try {
                settings.setYoutrackPassword(PasswordUtil.decryptEncryptedPassword(encryptedPassword));
            } catch (GeneralSecurityException e) {
                LOGGER.error("Could not decrypt password from settings file", e);
                throw ExceptionUtil.getIllegalStateException("exceptions.settings.password.decrypt", e);
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

        String autoloadDataTimerangeAsString = properties.getProperty(AUTOLOAD_DATA_TIMERANGE_PROPERTY);
        if (StringUtils.isNotBlank(autoloadDataTimerangeAsString)) {
            try {
                ReportTimerange reportTimerange = ReportTimerange.valueOf(autoloadDataTimerangeAsString);
                settings.setLastUsedReportTimerange(reportTimerange);
            } catch (IllegalArgumentException e) {
                LOGGER.warn("Could not determine ReportTimerange by settings value {}", autoloadDataTimerangeAsString);
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

        if (StringUtils.isNotBlank(settings.getYoutrackUrl())) {
            properties.setProperty(YOUTRACK_URL_PROPERTY, settings.getYoutrackUrl());
        }

        if (StringUtils.isNotBlank(settings.getYoutrackUsername())) {
            properties.setProperty(YOUTRACK_USERNAME_PROPERTY, settings.getYoutrackUsername());
        }

        if (StringUtils.isNotBlank(settings.getYoutrackPassword())) {
            try {
                properties.setProperty(YOUTRACK_PASSWORD_PROPERTY, PasswordUtil.encryptCleartextPassword(settings.getYoutrackPassword()));
            } catch (GeneralSecurityException e) {
                LOGGER.error("Could not encrypt password for settings file", e);
                throw ExceptionUtil.getIllegalStateException("exceptions.settings.password.encrypt", e);
            }
        }

        if (settings.getLastUsedReportTimerange() != null) {
            properties.setProperty(AUTOLOAD_DATA_TIMERANGE_PROPERTY, settings.getLastUsedReportTimerange().name());
        }

        return properties;
    }

    public static class Settings {

        private int windowWidth = 800;

        private int windowHeight = 600;

        private int windowX = 0;

        private int windowY = 0;

        private int workHoursADay = 8;

        private String youtrackUrl;

        private String youtrackUsername;

        private String youtrackPassword;

        private boolean loadDataAtStartup;

        private ReportTimerange lastUsedReportTimerange;

        private boolean showStatistics = true;

        private boolean showAllWorklogs = true;

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

        public boolean hasMissingConnectionParameters() {
            return StringUtils.isBlank(settings.getYoutrackUrl()) ||
                   StringUtils.isBlank(settings.getYoutrackUsername()) ||
                   StringUtils.isBlank(settings.getYoutrackPassword());
        }
    }

}
