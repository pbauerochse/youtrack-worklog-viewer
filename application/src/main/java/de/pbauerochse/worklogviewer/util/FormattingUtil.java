package de.pbauerochse.worklogviewer.util;

import de.pbauerochse.worklogviewer.settings.SettingsUtil;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class FormattingUtil {

    public static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("i18n/ytwv");

    private static DateTimeFormatter dateFormatter;
    private static DateTimeFormatter longDateFormatter;
    private static DateTimeFormatter dateTimeFormatter;
    private static NumberFormat percentageFormatter;

    public static String formatMinutes(long minutes) {
        return formatMinutes(minutes, false);
    }

    public static String formatMinutes(long minutes, boolean full) {
        return new WorklogTimeFormatter(SettingsUtil.getSettingsViewModel().getWorkhoursProperty().get()).getFormatted(minutes, full);
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
        if (date == null) return "";

        if (dateFormatter == null) {
            dateFormatter = DateTimeFormatter.ofPattern(getFormatted("date.column.format"));
        }
        return dateFormatter.format(date);
    }

    public static String formatLongDate(LocalDate date) {
        if (date == null) return "";

        if (longDateFormatter == null) {
            longDateFormatter = DateTimeFormatter.ofPattern(getFormatted("date.column.format.long"));
        }
        return longDateFormatter.format(date);
    }


}
