package de.pbauerochse.worklogviewer.youtrack.domain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Patrick Bauerochse
 * @since 01.04.15
 */
public class WorklogReport {

    private Map<String, TaskWithWorklogs> worklogSummaryMap = Maps.newHashMap();

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

    public ImmutableList<TaskWithWorklogs> getTasks() {
        return ImmutableList.copyOf(worklogSummaryMap.values());
    }

    public ImmutableList<String> getDistinctProjectNames() {
        return ImmutableList.copyOf(
            worklogSummaryMap.values().stream()
                    .map(TaskWithWorklogs::getProject)
                    .distinct()
                    .collect(Collectors.toList())
        );
    }
}
