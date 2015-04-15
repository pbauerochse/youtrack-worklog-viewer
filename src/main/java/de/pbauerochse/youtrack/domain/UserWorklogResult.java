package de.pbauerochse.youtrack.domain;

import org.apache.commons.lang3.StringUtils;

import java.text.Collator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Patrick Bauerochse
 * @since 01.04.15
 */
public class UserWorklogResult {

    private static final Collator COLLATOR = Collator.getInstance(Locale.GERMANY);

    private String username;

    private Map<String, UserTaskWorklogs> worklogSummaryMap = new HashMap<>(100);

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserTaskWorklogs getWorklog(String taskId) {
        if (StringUtils.isBlank(taskId)) throw new IllegalArgumentException("TaskId must not be null or empty");
        return worklogSummaryMap.get(taskId);
    }

    public void addWorklogSummary(String taskId, UserTaskWorklogs summary) {
        if (StringUtils.isBlank(taskId)) throw new IllegalArgumentException("TaskId must not be null or empty");
        if (summary == null) throw new IllegalArgumentException("Summary must not be null");
        if (worklogSummaryMap.containsKey(taskId)) throw new IllegalStateException("Already added WorklogSummary for Task " + taskId);
        worklogSummaryMap.put(taskId, summary);
    }

    public List<UserTaskWorklogs> getSummariesAsList() {
        UserTaskWorklogs summary = new UserTaskWorklogs(true);

        List<UserTaskWorklogs> sortedAndMapped = worklogSummaryMap
                .entrySet()
                .stream()
                .map(stringUserWorklogSummaryEntry -> stringUserWorklogSummaryEntry.getValue())
                .sorted((o1, o2) -> COLLATOR.compare(o1.getIssue(), o2.getIssue()))
                .peek(userTaskWorklogs -> userTaskWorklogs
                        .getWorklogItemList()
                        .stream()
                        .forEach(worklogItem -> summary.getWorklogItemList().add(worklogItem)))
                .collect(Collectors.toList());

        sortedAndMapped.add(summary);
        return sortedAndMapped;
    }


}
