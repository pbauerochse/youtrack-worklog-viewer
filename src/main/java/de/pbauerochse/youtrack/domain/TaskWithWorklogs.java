package de.pbauerochse.youtrack.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrick Bauerochse
 * @since 01.04.15
 */
public class TaskWithWorklogs {

    private String issue;
    private String summary;
    private long estimatedWorktimeInMinutes;

    private List<WorklogItem> worklogItemList = new ArrayList<>(50);

    private boolean isSummaryRow;

    public TaskWithWorklogs(boolean isSummaryRow) {
        this.isSummaryRow = isSummaryRow;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public long getEstimatedWorktimeInMinutes() {
        return estimatedWorktimeInMinutes;
    }

    public void setEstimatedWorktimeInMinutes(long estimatedWorktimeInMinutes) {
        this.estimatedWorktimeInMinutes = estimatedWorktimeInMinutes;
    }

    public List<WorklogItem> getWorklogItemList() {
        return worklogItemList;
    }

    public boolean isSummaryRow() {
        return isSummaryRow;
    }

    public long getTotalInMinutes() {
        long sum = 0;

        for (WorklogItem worklogItem : worklogItemList) {
            sum += worklogItem.getDurationInMinutes();
        }

        return sum;
    }

    public long getTotalInMinutes(LocalDate atDate) {
        long sum = 0;

        for (WorklogItem worklogItem : worklogItemList) {
            if (worklogItem.getDate().isEqual(atDate)) {
                sum += worklogItem.getDurationInMinutes();
            }
        }

        return sum;
    }

}
