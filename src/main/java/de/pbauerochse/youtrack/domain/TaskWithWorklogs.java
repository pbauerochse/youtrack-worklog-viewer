package de.pbauerochse.youtrack.domain;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrick Bauerochse
 * @since 01.04.15
 */
public class TaskWithWorklogs {

    private String project;
    private String issue;
    private String summary;
    private long estimatedWorktimeInMinutes;

    private List<WorklogItem> worklogItemList = new ArrayList<>(50);
    private List<String> distinctGroupCriteria = new ArrayList<>(50);

    private boolean isSummaryRow;
    private boolean isGroupRow;

    public TaskWithWorklogs(boolean isSummaryRow) {
        this.isSummaryRow = isSummaryRow;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
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

    public void addWorklogItem(WorklogItem item) {
        if (item == null) throw new IllegalArgumentException("WorklogItem must not be null");

        if (!getWorklogItemList().contains(item)) {
            getWorklogItemList().add(item);

            if (StringUtils.isNotBlank(item.getGroup()) && !distinctGroupCriteria.contains(item.getGroup())) {
                distinctGroupCriteria.add(item.getGroup());
            }
        }
    }

    public void setIsGroupRow(boolean isGroupRow) {
        this.isGroupRow = isGroupRow;
    }

    public boolean isGroupRow() {
        return isGroupRow;
    }

    public List<String> getDistinctGroupCriteria() {
        return distinctGroupCriteria;
    }

    public TaskWithWorklogs createCopy() {
        TaskWithWorklogs copy = new TaskWithWorklogs(isSummaryRow);
        copy.setProject(getProject());
        copy.setIssue(getIssue());
        copy.setSummary(getSummary());
        copy.setEstimatedWorktimeInMinutes(getEstimatedWorktimeInMinutes());
        copy.setIsGroupRow(isGroupRow());
        copy.getDistinctGroupCriteria().addAll(getDistinctGroupCriteria());

        getWorklogItemList().forEach(worklogItem -> copy.addWorklogItem(worklogItem.createCopy()));

        return copy;
    }
}
