package de.pbauerochse.youtrack.domain;

import org.apache.commons.lang3.StringUtils;

import java.text.Collator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Patrick Bauerochse
 * @since 01.04.15
 */
public class WorklogResult {

    private static final Collator COLLATOR = Collator.getInstance(Locale.GERMANY);
    private static final Pattern PROJECT_ID_PATTERN = Pattern.compile("^(.+)-\\d+$");

    private Map<String, TaskWithWorklogs> worklogSummaryMap = new HashMap<>(100);

    private String username;

    private boolean finalized = false;

    // populate in finalize()

    private List<TaskWithWorklogs> ownSummary;
    private List<TaskWithWorklogs> totalSummary;
    private List<String> distinctProjects;
    private Map<String, List<TaskWithWorklogs>> projectToTasksMap;

    public WorklogResult(String username) {
        this.username = username;
    }

    public TaskWithWorklogs getWorklog(String taskId) {
        if (StringUtils.isBlank(taskId)) throw new IllegalArgumentException("TaskId must not be null or empty");
        return worklogSummaryMap.get(taskId);
    }

    public void addWorklogSummary(String taskId, TaskWithWorklogs summary) {
        if (finalized) throw new IllegalArgumentException("WorklogSummary alread finalized");
        if (StringUtils.isBlank(taskId)) throw new IllegalArgumentException("TaskId must not be null or empty");
        if (summary == null) throw new IllegalArgumentException("Summary must not be null");
        if (worklogSummaryMap.containsKey(taskId))
            throw new IllegalStateException("Already added WorklogSummary for Task " + taskId);
        worklogSummaryMap.put(taskId, summary);
    }

    public void finalize() {
        if (!finalized) {
            generateOwnSummaryList();
            generateDistinctProjectsList();
            generateProjectSummaryMap();
            generateTotalSummaryList();
            finalized = true;
        }
    }

    private void generateOwnSummaryList() {
        TaskWithWorklogs summary = new TaskWithWorklogs(true);

        ownSummary = worklogSummaryMap.values().stream()
                .filter(taskWithWorklogs -> {
                    // only those entries where the username matches
                    for (WorklogItem worklogItem : taskWithWorklogs.getWorklogItemList()) {
                        if (StringUtils.equals(worklogItem.getUsername(), username)) {
                            return true;
                        }
                    }

                    return false;
                })
                .sorted((o1, o2) -> COLLATOR.compare(o1.getIssue(), o2.getIssue()))
                .peek(taskWithWorklogs -> taskWithWorklogs
                        .getWorklogItemList()
                        .stream()
                        .forEach(worklogItem -> {
                            if (StringUtils.equals(worklogItem.getUsername(), username)) {
                                summary.getWorklogItemList().add(worklogItem);
                            }
                        }))
                .collect(Collectors.toList());

        ownSummary.add(summary);
    }

    private void generateDistinctProjectsList() {
        distinctProjects = worklogSummaryMap.values().stream()
                .map(taskWithWorklogs -> {
                    Matcher matcher = PROJECT_ID_PATTERN.matcher(taskWithWorklogs.getIssue());
                    if (matcher.matches()) {
                        return matcher.group(1);
                    }

                    throw new IllegalStateException("Could not match pattern for " + taskWithWorklogs.getIssue());
                })
                .distinct()
                .sorted((o1, o2) -> COLLATOR.compare(o1, o2))
                .collect(Collectors.toList());
    }

    private void generateProjectSummaryMap() {
        projectToTasksMap = new HashMap<>(distinctProjects.size());
        distinctProjects.forEach(project -> {

            TaskWithWorklogs projectSummary = new TaskWithWorklogs(true);

            List<TaskWithWorklogs> projectWorklogs = worklogSummaryMap.values().stream()
                    .filter(taskWithWorklogs -> StringUtils.startsWith(taskWithWorklogs.getIssue(), project))
                    .sorted((o1, o2) -> COLLATOR.compare(o1.getIssue(), o2.getIssue()))
                    .peek(userTaskWorklogs -> userTaskWorklogs
                            .getWorklogItemList()
                            .stream()
                            .forEach(worklogItem -> projectSummary.getWorklogItemList().add(worklogItem)))
                    .collect(Collectors.toList());

            projectWorklogs.add(projectSummary);
            projectToTasksMap.put(project, projectWorklogs);
        });
    }

    private void generateTotalSummaryList() {
        TaskWithWorklogs summary = new TaskWithWorklogs(true);

        totalSummary = worklogSummaryMap
                .values()
                .stream()
                .sorted((o1, o2) -> COLLATOR.compare(o1.getIssue(), o2.getIssue()))
                .peek(userTaskWorklogs -> userTaskWorklogs
                        .getWorklogItemList()
                        .stream()
                        .forEach(worklogItem -> summary.getWorklogItemList().add(worklogItem)))
                .collect(Collectors.toList());

        totalSummary.add(summary);
    }

    public List<TaskWithWorklogs> getOwnSummary() {
        if (!finalized) throw new IllegalStateException("Not finalized yet");
        return ownSummary;
    }

    public List<String> getDistinctProjects() {
        if (!finalized) throw new IllegalStateException("Not finalized yet");
        return distinctProjects;
    }

    public List<TaskWithWorklogs> getProjectSummary(String projectPrefix) {
        if (!finalized) throw new IllegalStateException("Not finalized yet");
        return projectToTasksMap.get(projectPrefix);
    }

    public List<TaskWithWorklogs> getAll() {
        if (!finalized) throw new IllegalStateException("Not finalized yet");
        return totalSummary;
    }


}
