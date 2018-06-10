package de.pbauerochse.worklogviewer.fx.tabs;

/**
 * @author Patrick Bauerochse
 * @since 02.07.15
 */
class ProjectWorklogTab extends WorklogsTab {

//    private Optional<List<TaskWithWorklogs>> resultItemsToDisplay = Optional.empty();

    ProjectWorklogTab() {
        super("");
    }

//    @Override
//    protected List<TaskWithWorklogs> getFilteredList(List<TaskWithWorklogs> tasks) {
//        return tasks.stream()
//                .filter(taskWithWorklogs -> taskWithWorklogs.getProject().equals(getText()))
//                .sorted((o1, o2) -> COLLATOR.compare(o1.getIssue(), o2.getIssue()))
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    void update(@NotNull TimeReport timeReport) {
//        // TODO implement
//    }

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
}
