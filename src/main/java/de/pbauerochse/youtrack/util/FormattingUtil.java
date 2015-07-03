package de.pbauerochse.youtrack.util;

import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * @author Patrick Bauerochse
 * @since 14.04.15
 */
public class FormattingUtil {

    public static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("i18n/ytwv");

    private static DateTimeFormatter dateTimeFormatter;
    private static final int MINUTES_PER_HOUR = 60;


    public static String formatMinutes(long minutes) {
        return formatMinutes(minutes, false);
    }

    /**
     * Formats the given amount of minutes in Jira Style format
     *
     * @param minutes
     * @return
     */
    public static String formatMinutes(long minutes, boolean full) {
        StringBuilder worklogFormatted = new StringBuilder();

        int workhours = SettingsUtil.loadSettings().getWorkHoursADay();

        if (workhours == 0) {
            throw ExceptionUtil.getIllegalStateException("exceptions.main.workhours.zero");
        }

        final int minutesPerWorkday = workhours * MINUTES_PER_HOUR;

        long days = minutes / minutesPerWorkday;
        long remainingMinutes = minutes % minutesPerWorkday;

        long hours = remainingMinutes / MINUTES_PER_HOUR;
        remainingMinutes = remainingMinutes % MINUTES_PER_HOUR;

        if (days > 0 || full) {
            worklogFormatted.append(days).append('d');
        }

        if (hours > 0 || full) {
            if (worklogFormatted.length() > 0) {
                worklogFormatted.append(' ');
            }

            worklogFormatted.append(hours).append('h');
        }

        if (remainingMinutes > 0 || full) {
            if (worklogFormatted.length() > 0) {
                worklogFormatted.append(' ');
            }

            worklogFormatted.append(remainingMinutes).append('m');
        }

        return worklogFormatted.toString();
    }

    public static String getFormatted(String messageKey) {
        return RESOURCE_BUNDLE.getString(messageKey);
    }

    public static String getFormatted(String messageKey, Object... parameters) {
        return MessageFormat.format(RESOURCE_BUNDLE.getString(messageKey), parameters);
    }

    public static String formatDate(LocalDate date) {
        if (date == null) return StringUtils.EMPTY;

        if (dateTimeFormatter == null) {
            dateTimeFormatter = DateTimeFormatter.ofPattern(getFormatted("date.column.format"));
        }
        return dateTimeFormatter.format(date);
    }

}
