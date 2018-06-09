package de.pbauerochse.worklogviewer.youtrack.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author Patrick Bauerochse
 * @since 01.04.15
 */
class TaskWithWorklogs {

    private String project;
    private String issue;
    private String summary;
    private long estimatedWorktimeInMinutes;
    private LocalDateTime resolved;

    private List<WorklogItem> worklogItemList = new ArrayList<>();

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

    public LocalDateTime getResolved() {
        return resolved;
    }

    public void setResolved(LocalDateTime resolved) {
        this.resolved = resolved;
    }

    public List<WorklogItem> getWorklogItemList() {
        return worklogItemList;
    }

    public long getTotalInMinutes() {
        AtomicLong sum = new AtomicLong(0);
        worklogItemList.forEach(item -> sum.addAndGet(item.getDurationInMinutes()));
        return sum.longValue();
    }

    public long getTotalInMinutes(LocalDate atDate) {
        AtomicLong sum = new AtomicLong(0);

        worklogItemList.stream()
                .filter(item -> item.getDate().isEqual(atDate))
                .forEach(item -> sum.addAndGet(item.getDurationInMinutes()));

        return sum.longValue();
    }

    public List<String> getDistinctGroupByCriteriaValues() {
        return getWorklogItemList().stream()
                        .map(WorklogItem::getGroup)
                        .distinct()
                        .collect(Collectors.toList());
    }

//    public TaskWithWorklogs createDeepCopy() {
//        TaskWithWorklogs copy = new TaskWithWorklogs();
//        copy.setEstimatedWorktimeInMinutes(getEstimatedWorktimeInMinutes());
//        copy.setIssue(getIssue());
//        copy.setProject(getProject());
//        copy.setSummary(getSummary());
//        copy.setResolved(getResolved());
//
//        getWorklogItemList().forEach(worklogItem -> copy.getWorklogItemList().add(worklogItem.createDeepCopy()));
//
//        return copy;
//    }
}
