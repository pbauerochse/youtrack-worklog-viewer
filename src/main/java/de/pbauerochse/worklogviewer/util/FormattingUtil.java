package de.pbauerochse.worklogviewer.util;

import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * @author Patrick Bauerochse
 * @since 14.04.15
 */
public class FormattingUtil {

    public static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("i18n/ytwv");

    private static DateTimeFormatter dateFormatter;
    private static DateTimeFormatter dateTimeFormatter;

    private static NumberFormat percentageFormatter;
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

        if (days > 0) {
            worklogFormatted.append(days).append('d');
        }

        if (hours > 0 || (full && days > 0)) {
            if (worklogFormatted.length() > 0) {
                worklogFormatted.append(' ');
            }

            worklogFormatted.append(hours).append('h');
        }

        if (remainingMinutes > 0 || (full && (hours > 0 || days > 0))) {
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

    public static String formatPercentage(double percentage) {
        if (percentageFormatter == null) {
            percentageFormatter = new DecimalFormat("#0.0%");
        }

        return percentageFormatter.format(percentage);
    }

    public static String formatDate(LocalDate date) {
        if (date == null) return StringUtils.EMPTY;

        if (dateFormatter == null) {
            dateFormatter = DateTimeFormatter.ofPattern(getFormatted("date.column.format"));
        }
        return dateFormatter.format(date);
    }

    public static String formatDateTime(LocalDateTime date) {
        if (date == null) return StringUtils.EMPTY;

        if (dateTimeFormatter == null) {
            dateTimeFormatter = DateTimeFormatter.ofPattern(getFormatted("datetime.column.format"));
        }
        return dateTimeFormatter.format(date);
    }

}
