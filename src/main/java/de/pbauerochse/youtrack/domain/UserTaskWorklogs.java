package de.pbauerochse.youtrack.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrick Bauerochse
 * @since 01.04.15
 */
public class UserTaskWorklogs {

    private String issue;
    private String summary;
    private List<WorklogItem> worklogItemList = new ArrayList<>(50);

    private boolean isSummaryRow;

    public UserTaskWorklogs(boolean isSummaryRow) {
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
