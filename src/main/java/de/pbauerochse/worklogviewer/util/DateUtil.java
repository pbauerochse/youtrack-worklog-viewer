package de.pbauerochse.worklogviewer.util;

import java.time.*;

/**
 * Created by patrick on 31.10.15.
 */
public class DateUtil {

    public static LocalDate getDate(long dateAsLong) {
        return getDateTime(dateAsLong).toLocalDate();
    }

    public static LocalDateTime getDateTime(long dateAsLong) {
        ZonedDateTime utc = ZonedDateTime.ofInstant(Instant.ofEpochMilli(dateAsLong), ZoneId.of("UTC"));
        return utc.toLocalDateTime();
    }

}
