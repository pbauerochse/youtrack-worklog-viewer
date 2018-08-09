package de.pbauerochse.worklogviewer.settings.loaders;

import de.pbauerochse.worklogviewer.connector.YouTrackVersion;
import de.pbauerochse.worklogviewer.domain.ReportTimerange;
import de.pbauerochse.worklogviewer.settings.Settings;
import de.pbauerochse.worklogviewer.util.EncryptionUtil;
import de.pbauerochse.worklogviewer.util.ExceptionUtil;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

/**
 * Loads the old properties settings
 * file into an Settings object
 */
public class PropertiesSettingsLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesSettingsLoader.class);

    private static final String WINDOW_X_PROPERTY = "window.x";
    private static final String WINDOW_Y_PROPERTY = "window.y";
    private static final String WINDOW_WIDTH_PROPERTY = "window.width";
    private static final String WINDOW_HEIGHT_PROPERTY = "window.height";
    private static final String WORK_HOURS_PROPERTY = "workhours";
    private static final String YOUTRACK_VERSION_PROPERTY = "youtrackversion";
    private static final String YOUTRACK_USERNAME_PROPERTY = "username";
    private static final String YOUTRACK_URL_PROPERTY = "youtrackurl";
    private static final String YOUTRACK_PERMANENT_TOKEN = "permanent_token";
    private static final String SHOW_ALL_WORKLOGS_PROPERTY = "showonlyowntimelogs.enabled";
    private static final String SHOW_STATISTICS_PROPERTY = "statistics.enabled";
    private static final String AUTOLOAD_DATA_PROPERTY = "autoload.enabled";
    private static final String AUTOLOAD_DATA_TIMERANGE_PROPERTY = "autoload.timerange";
    private static final String SHOW_DECIMAL_HOURS_IN_EXCEL_REPORT = "excel.decimaltimes";
    private static final String COLLAPSE_STATE_PROPERTY = "collapse.state";
    private static final String HIGHLIGHT_STATE_PROPERTY = "highlight.state";

    private final File propertiesFile;

    public PropertiesSettingsLoader(@NotNull File propertiesFile) {
        this.propertiesFile = propertiesFile;
    }

    public Settings load() {
        Properties properties = loadPropertiesFile();
        return fromProperties(properties);
    }

    private Properties loadPropertiesFile() {
        LOGGER.debug("Loading configuration from {}", propertiesFile.getAbsolutePath());
        Properties properties = new Properties();

        try (InputStream inputStream = new FileInputStream(propertiesFile)) {
            properties.load(inputStream);
        } catch (IOException e) {
            LOGGER.error("Could not read settings from {}", propertiesFile.getAbsolutePath(), e);
            throw ExceptionUtil.getRuntimeException("exceptions.settings.read", e, propertiesFile.getAbsolutePath());
        }

        return properties;
    }

    private Settings fromProperties(Properties properties) {
        Settings settings = new Settings();

        applyWindowSettings(settings, properties);
        applyYouTrackConnectionSettings(settings, properties);
        applyGeneralSettings(settings, properties);

        return settings;
    }

    private void applyYouTrackConnectionSettings(Settings settings, Properties properties) {
        // youtrack version
        String youtrackVersionAsString = properties.getProperty(YOUTRACK_VERSION_PROPERTY);
        if (StringUtils.isNotBlank(youtrackVersionAsString)) {
            YouTrackVersion version = getYouTrackVersion(youtrackVersionAsString);
            settings.getYouTrackConnectionSettings().setVersion(version);
        }

        settings.getYouTrackConnectionSettings().setUrl(properties.getProperty(YOUTRACK_URL_PROPERTY));
        settings.getYouTrackConnectionSettings().setUsername(properties.getProperty(YOUTRACK_USERNAME_PROPERTY));

        String encryptedPermanentToken = properties.getProperty(YOUTRACK_PERMANENT_TOKEN);
        if (StringUtils.isNotBlank(encryptedPermanentToken)) {
            try {
                settings.getYouTrackConnectionSettings().setPermanentToken(EncryptionUtil.decryptEncryptedString(encryptedPermanentToken));
            } catch (GeneralSecurityException e) {
                LOGGER.error("Could not decrypt permanent token from settings file", e);
                throw ExceptionUtil.getIllegalStateException("exceptions.settings.permanenttoken.decrypt", e);
            }
        }
    }

    private YouTrackVersion getYouTrackVersion(String youtrackVersionAsString) {
        switch (youtrackVersionAsString) {
            case "PRE_2017":
                return de.pbauerochse.worklogviewer.connector.v2017.SupportedVersions.getV2017_4();
            case "POST_2017":
            case "POST_2018":
                return de.pbauerochse.worklogviewer.connector.v2017.SupportedVersions.getV2018_1();

            default:
                return null;
        }
    }

    private void applyWindowSettings(Settings settings, Properties properties) {
        String windowXAsString = properties.getProperty(WINDOW_X_PROPERTY);
        if (StringUtils.isNotBlank(windowXAsString)) {
            try {
                settings.getWindowSettings().setPositionX(parseInt(windowXAsString));
            } catch (NumberFormatException e) {
                // ignore
                LOGGER.warn("Could not convert {} to Integer for setting {}", windowXAsString, WINDOW_X_PROPERTY);
            }
        }

        String windowYAsString = properties.getProperty(WINDOW_Y_PROPERTY);
        if (StringUtils.isNotBlank(windowYAsString)) {
            try {
                settings.getWindowSettings().setPositionY(parseInt(windowYAsString));
            } catch (NumberFormatException e) {
                // ignore
                LOGGER.warn("Could not convert {} to Integer for setting {}", windowYAsString, WINDOW_Y_PROPERTY);
            }
        }

        String windowWidthAsString = properties.getProperty(WINDOW_WIDTH_PROPERTY);
        if (StringUtils.isNotBlank(windowWidthAsString)) {
            try {
                settings.getWindowSettings().setWidth(parseInt(windowWidthAsString));
            } catch (NumberFormatException e) {
                // ignore
                LOGGER.warn("Could not convert {} to Integer for setting {}", windowWidthAsString, WINDOW_WIDTH_PROPERTY);
            }
        }

        String windowHeightAsString = properties.getProperty(WINDOW_HEIGHT_PROPERTY);
        if (StringUtils.isNotBlank(windowHeightAsString)) {
            try {
                settings.getWindowSettings().setHeight(parseInt(windowHeightAsString));
            } catch (NumberFormatException e) {
                // ignore
                LOGGER.warn("Could not convert {} to Integer for setting {}", windowHeightAsString, WINDOW_HEIGHT_PROPERTY);
            }
        }
    }

    private void applyGeneralSettings(Settings settings, Properties properties) {
        String workHoursAsString = properties.getProperty(WORK_HOURS_PROPERTY);
        if (StringUtils.isNotBlank(workHoursAsString)) {
            try {
                settings.setWorkHoursADay(parseInt(workHoursAsString));
            } catch (NumberFormatException e) {
                // ignore
                LOGGER.warn("Could not convert {} to Integer for setting {}", workHoursAsString, WORK_HOURS_PROPERTY);
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
                settings.getCollapseState().set(getDayOfWeeksFromBitmask(parseInt(collapseStateAsString)));
            } catch (NumberFormatException e) {
                LOGGER.warn("Could not get collapse state from {}", collapseStateAsString);
            }
        }

        String highlightStateAsString = properties.getProperty(HIGHLIGHT_STATE_PROPERTY);
        if (StringUtils.isNotBlank(highlightStateAsString)) {
            try {
                settings.getHighlightState().set(getDayOfWeeksFromBitmask(parseInt(highlightStateAsString)));
            } catch (NumberFormatException e) {
                LOGGER.warn("Could not get highlight state from {}", highlightStateAsString);
            }
        }
    }

    private Set<DayOfWeek> getDayOfWeeksFromBitmask(int bitmask) {
        return Arrays.stream(DayOfWeek.values())
                .filter(dayOfWeek -> isBitSet(dayOfWeek, bitmask))
                .collect(Collectors.toSet());
    }

    private boolean isBitSet(DayOfWeek value, int bitmask) {
        int bitValue = 1 << value.ordinal();
        return (bitmask & bitValue) == bitValue;
    }

}
