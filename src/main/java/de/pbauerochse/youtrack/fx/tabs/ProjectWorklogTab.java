package de.pbauerochse.youtrack.fx.tabs;

import de.pbauerochse.youtrack.domain.TaskWithWorklogs;
import de.pbauerochse.youtrack.domain.WorklogResult;
import javafx.scene.Node;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Patrick Bauerochse
 * @since 02.07.15
 */
public class ProjectWorklogTab extends WorklogTab {

    private Optional<List<TaskWithWorklogs>> resultItemsToDisplay = Optional.empty();

    public ProjectWorklogTab(String projectName) {
        super(projectName);
    }

    @Override
    protected List<TaskWithWorklogs> getDisplayResult(WorklogResult result) {
        if (!resultItemsToDisplay.isPresent() || resultToDisplayChangedSinceLastRender) {

            TaskWithWorklogs projectSummary = new TaskWithWorklogs(true);

            List<TaskWithWorklogs> projectWorklogs = result.getWorklogSummaryMap()
                    .values().stream()
                    .filter(taskWithWorklogs -> StringUtils.startsWith(taskWithWorklogs.getIssue(), getText()))
                    .sorted((o1, o2) -> COLLATOR.compare(o1.getIssue(), o2.getIssue()))
                    .peek(userTaskWorklogs -> userTaskWorklogs
                            .getWorklogItemList()
                            .stream()
                            .forEach(worklogItem -> projectSummary.getWorklogItemList().add(worklogItem)))
                    .collect(Collectors.toList());

            projectWorklogs.add(projectSummary);

            resultItemsToDisplay = Optional.of(projectWorklogs);
        }

        return resultItemsToDisplay.get();
    }

    @Override
    protected Node getStatisticsView() {
        // TODO macht et!
        return super.getStatisticsView();
    }
}
