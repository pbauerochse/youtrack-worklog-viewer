package de.pbauerochse.youtrack.domain;

import java.time.LocalDate;

/**
 * @author Patrick Bauerochse
 * @since 01.04.15
 */
public class WorklogItem {

    private String workDescription;
    private LocalDate date;
    private long durationInMinutes;
    private String username;
    private String userDisplayname;
    private String workType;
    private String group;

    public String getWorkDescription() {
        return workDescription;
    }

    public void setWorkDescription(String workDescription) {
        this.workDescription = workDescription;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public long getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(long durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserDisplayname() {
        return userDisplayname;
    }

    public void setUserDisplayname(String userDisplayname) {
        this.userDisplayname = userDisplayname;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public WorklogItem createCopy() {
        WorklogItem copy = new WorklogItem();
        copy.setWorkDescription(getWorkDescription());
        copy.setDate(getDate());
        copy.setDurationInMinutes(getDurationInMinutes());
        copy.setUsername(getUsername());
        copy.setUserDisplayname(getUserDisplayname());
        copy.setWorkType(getWorkType());
        copy.setGroup(getGroup());

        return copy;
    }
}
