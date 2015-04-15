package de.pbauerochse.youtrack.util;

import org.apache.commons.lang3.StringUtils;

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

    private static final File CONFIG_FILE_LOCATION = new File(System.getProperty("user.home"), "youtrack-worklog.properties");

    private static final String WINDOW_X_PROPERTY = "window.x";
    private static final String WINDOW_Y_PROPERTY = "window.y";
    private static final String WINDOW_WIDTH_PROPERTY = "window.width";
    private static final String WINDOW_HEIGHT_PROPERTY = "window.height";
    private static final String YOUTRACK_USERNAME_PROPERTY = "username";
    private static final String YOUTRACK_PASSWORD_PROPERTY = "password";
    private static final String YOUTRACK_URL_PROPERTY = "youtrackurl";
    private static final String WORK_HOURS_PROPERTY = "workhours";

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
                Properties properties = new Properties();
                try {
                    properties.load(new FileInputStream(CONFIG_FILE_LOCATION));
                    applyFromPropertiesToSettings(settings, properties);
                } catch (IOException e) {
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
                CONFIG_FILE_LOCATION.createNewFile();
            } catch (IOException e) {
                throw ExceptionUtil.getRuntimeException("exceptions.settings.create", e, CONFIG_FILE_LOCATION.getAbsolutePath());
            }
        }

        Properties properties = getAsProperties(settings);
        try {
            properties.store(new FileOutputStream(CONFIG_FILE_LOCATION), "Settings file for YouTrack worklog viewer");
        } catch (IOException e) {
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
            }
        }

        String windowYAsString = properties.getProperty(WINDOW_Y_PROPERTY);
        if (StringUtils.isNotBlank(windowYAsString)) {
            try {
                settings.setWindowY(Integer.parseInt(windowYAsString));
            } catch (NumberFormatException e) {
                // ignore
            }
        }

        String windowWidthAsString = properties.getProperty(WINDOW_WIDTH_PROPERTY);
        if (StringUtils.isNotBlank(windowWidthAsString)) {
            try {
                settings.setWindowWidth(Integer.parseInt(windowWidthAsString));
            } catch (NumberFormatException e) {
                // ignore
            }
        }

        String windowHeightAsString = properties.getProperty(WINDOW_HEIGHT_PROPERTY);
        if (StringUtils.isNotBlank(windowHeightAsString)) {
            try {
                settings.setWindowHeight(Integer.parseInt(windowHeightAsString));
            } catch (NumberFormatException e) {
                // ignore
            }
        }

        String workHoursAsString = properties.getProperty(WORK_HOURS_PROPERTY);
        if (StringUtils.isNotBlank(workHoursAsString)) {
            try {
                settings.setWorkHoursADay(Integer.parseInt(workHoursAsString));
            } catch (NumberFormatException e) {
                // ignore
            }
        }

        settings.setYoutrackUrl(properties.getProperty(YOUTRACK_URL_PROPERTY));
        settings.setYoutrackUsername(properties.getProperty(YOUTRACK_USERNAME_PROPERTY));
        String encryptedPassword = properties.getProperty(YOUTRACK_PASSWORD_PROPERTY);
        if (StringUtils.isNotBlank(encryptedPassword)) {
            try {
                settings.setYoutrackPassword(PasswordUtil.decryptEncryptedPassword(encryptedPassword));
            } catch (GeneralSecurityException e) {
                throw ExceptionUtil.getIllegalStateException("exceptions.settings.password.decrypt", e);
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
                throw ExceptionUtil.getIllegalStateException("exceptions.settings.password.encrypt", e);
            }
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
    }

}
