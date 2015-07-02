package de.pbauerochse.youtrack.domain;

import java.time.LocalDate;

/**
 * @author Patrick Bauerochse
 * @since 01.04.15
 */
public class WorklogItem {

    private String username;
    private LocalDate date;
    private String description;
    private long durationInMinutes;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(long durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }
}
