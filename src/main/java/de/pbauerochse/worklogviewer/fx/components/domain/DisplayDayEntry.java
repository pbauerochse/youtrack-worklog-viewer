package de.pbauerochse.worklogviewer.fx.components.domain;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Patrick Bauerochse
 * @since 09.07.15
 */
public class DisplayDayEntry {

    private LocalDate date;
    private AtomicLong spentTime = new AtomicLong(0);

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public AtomicLong getSpentTime() {
        return spentTime;
    }
}
