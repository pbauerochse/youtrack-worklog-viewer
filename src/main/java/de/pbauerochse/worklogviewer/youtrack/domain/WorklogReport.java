package de.pbauerochse.worklogviewer.youtrack.domain;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Patrick Bauerochse
 * @since 01.04.15
 */
public class WorklogReport {

    private Map<String, TaskWithWorklogs> worklogSummaryMap = new HashMap<>();

    public TaskWithWorklogs getWorklog(String taskId) {
        if (StringUtils.isBlank(taskId)) throw new IllegalArgumentException("TaskId must not be null or empty");
        return worklogSummaryMap.get(taskId);
    }

    public void addWorklogSummary(TaskWithWorklogs summary) {
        if (summary == null) throw new IllegalArgumentException("Summary must not be null");
        if (StringUtils.isBlank(summary.getIssue())) throw new IllegalArgumentException("TaskId must not be null or empty");
        if (worklogSummaryMap.containsKey(summary.getIssue())) throw new IllegalStateException("Already added WorklogSummary for Task " + summary.getIssue());
        worklogSummaryMap.put(summary.getIssue(), summary);
    }

    public List<TaskWithWorklogs> getTasks() {
        return new ArrayList<>(worklogSummaryMap.values());
    }

    public List<String> getDistinctProjectNames() {
        return worklogSummaryMap.values().stream()
                    .map(TaskWithWorklogs::getProject)
                    .distinct()
                    .collect(Collectors.toList());
    }
}
