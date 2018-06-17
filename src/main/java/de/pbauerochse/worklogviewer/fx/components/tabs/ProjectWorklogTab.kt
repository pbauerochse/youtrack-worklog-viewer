package de.pbauerochse.worklogviewer.fx.components.tabs

import de.pbauerochse.worklogviewer.fx.components.statistics.data.TaskCountByUserAndProjectStatisticData
import de.pbauerochse.worklogviewer.fx.components.statistics.panels.TimePerProjectAndUserGraphStatistics
import de.pbauerochse.worklogviewer.fx.components.statistics.userprojecttable.TaskCountByUserAndProjectStatistics
import de.pbauerochse.worklogviewer.youtrack.domain.Issue
import javafx.scene.Node

/**
 * Tab, that displays the results for a single [Project]
 * independent of the work author
 */
internal class ProjectWorklogTab : WorklogsTab("") {

    override fun getStatistics(issues: List<Issue>): List<Node> {
        val data = TaskCountByUserAndProjectStatisticData(issues)

        return arrayListOf(
            TaskCountByUserAndProjectStatistics(data),
            TimePerProjectAndUserGraphStatistics(data)
        )
    }

}

//    @Override
//    protected List<TaskWithWorklogs> getDisplayResult(WorklogReport result) {
//        if (!resultItemsToDisplay.isPresent() || resultToDisplayChangedSinceLastRender) {
//
//            TaskWithWorklogs projectSummary = new TaskWithWorklogs(true);
//
//            List<TaskWithWorklogs> projectWorklogs = result.getWorklogSummaryMap()
//                    .values().stream()
//                    .filter(taskWithWorklogs -> StringUtils.startsWith(taskWithWorklogs.getIssue(), getText()))
//                    .sorted((o1, o2) -> COLLATOR.compare(o1.getIssue(), o2.getIssue()))
//                    .peek(userTaskWorklogs -> userTaskWorklogs
//                            .getWorklogItemList()
//                            .stream()
//                            .forEach(worklogItem -> projectSummary.addWorklogItem(worklogItem)))
//                    .collect(Collectors.toList());
//
//            projectWorklogs.add(projectSummary);
//
//            resultItemsToDisplay = Optional.of(projectWorklogs);
//        }
//
//        return resultItemsToDisplay.get();
//    }
