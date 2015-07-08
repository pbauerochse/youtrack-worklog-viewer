package de.pbauerochse.youtrack.domain;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Patrick Bauerochse
 * @since 01.04.15
 */
public class WorklogResult {

    private Map<String, TaskWithWorklogs> worklogSummaryMap = new HashMap<>(100);
    private List<String> distinctProjectNames = new ArrayList<>(100);
    private List<String> distinctGroupValues = new ArrayList<>(100);

    private ReportTimerange timerange;

    public WorklogResult(ReportTimerange timerange) {
        this.timerange = timerange;
    }

    public TaskWithWorklogs getWorklog(String taskId) {
        if (StringUtils.isBlank(taskId)) throw new IllegalArgumentException("TaskId must not be null or empty");
        return worklogSummaryMap.get(taskId);
    }

    public void addWorklogSummary(TaskWithWorklogs summary) {
        if (summary == null) throw new IllegalArgumentException("Summary must not be null");
        if (StringUtils.isBlank(summary.getIssue())) throw new IllegalArgumentException("TaskId must not be null or empty");
        if (worklogSummaryMap.containsKey(summary.getIssue())) throw new IllegalStateException("Already added WorklogSummary for Task " + summary.getIssue());

        if (!distinctProjectNames.contains(summary.getProject())) {
            distinctProjectNames.add(summary.getProject());
        }

        String summaryGroup = summary.getGroup();
        if (StringUtils.isNotBlank(summaryGroup) && !distinctGroupValues.contains(summaryGroup)) {
            distinctGroupValues.add(summaryGroup);
        }

        worklogSummaryMap.put(summary.getIssue(), summary);
    }

    public Map<String, TaskWithWorklogs> getWorklogSummaryMap() {
        return worklogSummaryMap;
    }

    public List<String> getDistinctProjectNames() {
        return distinctProjectNames;
    }

    public List<String> getDistinctGroupValues() {
        return distinctGroupValues;
    }

    public ReportTimerange getTimerange() {
        return timerange;
    }
}
