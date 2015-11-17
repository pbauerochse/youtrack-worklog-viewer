package de.pbauerochse.worklogviewer.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Created by patrick on 31.10.15.
 */
public class DateUtil {

    public static LocalDate getDate(long dateAsLong) {
        return getDateTime(dateAsLong).toLocalDate();
    }

    public static LocalDateTime getDateTime(long dateAsLong) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(dateAsLong), ZoneId.systemDefault());
    }

}
