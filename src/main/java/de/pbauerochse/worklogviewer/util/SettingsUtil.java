package de.pbauerochse.worklogviewer.util;

import de.pbauerochse.worklogviewer.domain.ReportTimerange;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
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
    private static final String SHOW_DECIMAL_HOURS_IN_EXCEL_REPORT = "excel.decimaltimes";

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
                settings.windowXProperty().setValue(Integer.parseInt(windowXAsString));
            } catch (NumberFormatException e) {
                // ignore
                LOGGER.warn("Could not convert {} to Integer for setting {}", windowXAsString, WINDOW_X_PROPERTY);
            }
        }

        String windowYAsString = properties.getProperty(WINDOW_Y_PROPERTY);
        if (StringUtils.isNotBlank(windowYAsString)) {
            try {
                settings.windowYProperty().setValue(Integer.parseInt(windowYAsString));
            } catch (NumberFormatException e) {
                // ignore
                LOGGER.warn("Could not convert {} to Integer for setting {}", windowYAsString, WINDOW_Y_PROPERTY);
            }
        }

        String windowWidthAsString = properties.getProperty(WINDOW_WIDTH_PROPERTY);
        if (StringUtils.isNotBlank(windowWidthAsString)) {
            try {
                settings.windowWidthProperty().setValue(Integer.parseInt(windowWidthAsString));
            } catch (NumberFormatException e) {
                // ignore
                LOGGER.warn("Could not convert {} to Integer for setting {}", windowWidthAsString, WINDOW_WIDTH_PROPERTY);
            }
        }

        String windowHeightAsString = properties.getProperty(WINDOW_HEIGHT_PROPERTY);
        if (StringUtils.isNotBlank(windowHeightAsString)) {
            try {
                settings.windowHeightProperty().setValue(Integer.parseInt(windowHeightAsString));
            } catch (NumberFormatException e) {
                // ignore
                LOGGER.warn("Could not convert {} to Integer for setting {}", windowHeightAsString, WINDOW_HEIGHT_PROPERTY);
            }
        }

        String workHoursAsString = properties.getProperty(WORK_HOURS_PROPERTY);
        if (StringUtils.isNotBlank(workHoursAsString)) {
            try {
                settings.workHoursADayProperty().setValue(Integer.parseInt(workHoursAsString));
            } catch (NumberFormatException e) {
                // ignore
                LOGGER.warn("Could not convert {} to Integer for setting {}", workHoursAsString, WORK_HOURS_PROPERTY);
            }
        }

        settings.youtrackUrlProperty().setValue(properties.getProperty(YOUTRACK_URL_PROPERTY));
        settings.youtrackUsernameProperty().setValue(properties.getProperty(YOUTRACK_USERNAME_PROPERTY));
        String encryptedPassword = properties.getProperty(YOUTRACK_PASSWORD_PROPERTY);
        if (StringUtils.isNotBlank(encryptedPassword)) {
            try {
                settings.youtrackPasswordProperty().setValue(PasswordUtil.decryptEncryptedPassword(encryptedPassword));
            } catch (GeneralSecurityException e) {
                LOGGER.error("Could not decrypt password from settings file", e);
                throw ExceptionUtil.getIllegalStateException("exceptions.settings.password.decrypt", e);
            }
        }

        String showOnlyOwnWorklogsAsString = properties.getProperty(SHOW_ALL_WORKLOGS_PROPERTY);
        if (StringUtils.isNotBlank(showOnlyOwnWorklogsAsString)) {
            settings.showAllWorklogsProperty().setValue(Boolean.valueOf(showOnlyOwnWorklogsAsString));
        }

        String showStatisticsAsString = properties.getProperty(SHOW_STATISTICS_PROPERTY);
        if (StringUtils.isNotBlank(showStatisticsAsString)) {
            settings.showStatisticsProperty().setValue(Boolean.valueOf(showStatisticsAsString));
        }

        String autoloadDataAsString = properties.getProperty(AUTOLOAD_DATA_PROPERTY);
        if (StringUtils.isNotBlank(autoloadDataAsString)) {
            settings.loadDataAtStartupProperty().setValue(Boolean.valueOf(autoloadDataAsString));
        }

        String showDecimalHoursInExcel = properties.getProperty(SHOW_DECIMAL_HOURS_IN_EXCEL_REPORT);
        if (StringUtils.isNotBlank(showDecimalHoursInExcel)) {
            settings.showDecimalHourTimesInExcelReportProperty().setValue(Boolean.valueOf(showDecimalHoursInExcel));
        }

        String autoloadDataTimerangeAsString = properties.getProperty(AUTOLOAD_DATA_TIMERANGE_PROPERTY);
        if (StringUtils.isNotBlank(autoloadDataTimerangeAsString)) {
            try {
                ReportTimerange reportTimerange = ReportTimerange.valueOf(autoloadDataTimerangeAsString);
                settings.lastUsedReportTimerangeProperty().setValue(reportTimerange);
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
        properties.setProperty(SHOW_ALL_WORKLOGS_PROPERTY, String.valueOf(settings.getShowAllWorklogs()));
        properties.setProperty(SHOW_STATISTICS_PROPERTY, String.valueOf(settings.getShowStatistics()));
        properties.setProperty(AUTOLOAD_DATA_PROPERTY, String.valueOf(settings.getLoadDataAtStartup()));
        properties.setProperty(SHOW_DECIMAL_HOURS_IN_EXCEL_REPORT, String.valueOf(settings.getShowDecimalHourTimesInExcelReport()));

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

        private IntegerProperty windowWidth = new SimpleIntegerProperty(800);

        private IntegerProperty windowHeight = new SimpleIntegerProperty(600);

        private IntegerProperty windowX = new SimpleIntegerProperty(0);

        private IntegerProperty windowY = new SimpleIntegerProperty(0);

        private IntegerProperty workHoursADay = new SimpleIntegerProperty(8);

        private StringProperty youtrackUrl = new SimpleStringProperty();

        private StringProperty youtrackUsername = new SimpleStringProperty();

        private StringProperty youtrackPassword = new SimpleStringProperty();

        private BooleanProperty loadDataAtStartup = new SimpleBooleanProperty(false);

        private SimpleObjectProperty<ReportTimerange> lastUsedReportTimerange = new SimpleObjectProperty<>(ReportTimerange.THIS_WEEK);

        private BooleanProperty showStatistics = new SimpleBooleanProperty(true);

        private BooleanProperty showAllWorklogs = new SimpleBooleanProperty(true);

        private BooleanProperty showDecimalHourTimesInExcelReport = new SimpleBooleanProperty(false);

        private BooleanBinding hasMissingConnectionParametersBinding = youtrackUrlProperty().isEmpty()
                .or(youtrackUsernameProperty().isEmpty())
                .or(youtrackPasswordProperty().isEmpty());

        public int getWindowWidth() {
            return windowWidth.get();
        }

        public IntegerProperty windowWidthProperty() {
            return windowWidth;
        }

        public int getWindowHeight() {
            return windowHeight.get();
        }

        public IntegerProperty windowHeightProperty() {
            return windowHeight;
        }

        public int getWindowX() {
            return windowX.get();
        }

        public IntegerProperty windowXProperty() {
            return windowX;
        }

        public int getWindowY() {
            return windowY.get();
        }

        public IntegerProperty windowYProperty() {
            return windowY;
        }

        public int getWorkHoursADay() {
            return workHoursADay.get();
        }

        public IntegerProperty workHoursADayProperty() {
            return workHoursADay;
        }

        public String getYoutrackUrl() {
            return youtrackUrl.get();
        }

        public StringProperty youtrackUrlProperty() {
            return youtrackUrl;
        }

        public String getYoutrackUsername() {
            return youtrackUsername.get();
        }

        public StringProperty youtrackUsernameProperty() {
            return youtrackUsername;
        }

        public String getYoutrackPassword() {
            return youtrackPassword.get();
        }

        public StringProperty youtrackPasswordProperty() {
            return youtrackPassword;
        }

        public boolean getLoadDataAtStartup() {
            return loadDataAtStartup.get();
        }

        public BooleanProperty loadDataAtStartupProperty() {
            return loadDataAtStartup;
        }

        public ReportTimerange getLastUsedReportTimerange() {
            return lastUsedReportTimerange.get();
        }

        public SimpleObjectProperty<ReportTimerange> lastUsedReportTimerangeProperty() {
            return lastUsedReportTimerange;
        }

        public boolean getShowStatistics() {
            return showStatistics.get();
        }

        public BooleanProperty showStatisticsProperty() {
            return showStatistics;
        }

        public boolean getShowAllWorklogs() {
            return showAllWorklogs.get();
        }

        public BooleanProperty showAllWorklogsProperty() {
            return showAllWorklogs;
        }

        public boolean getShowDecimalHourTimesInExcelReport() {
            return showDecimalHourTimesInExcelReport.get();
        }

        public BooleanProperty showDecimalHourTimesInExcelReportProperty() {
            return showDecimalHourTimesInExcelReport;
        }

        public BooleanBinding hasMissingConnectionParameters() {
            return hasMissingConnectionParametersBinding;
        }
    }

}
