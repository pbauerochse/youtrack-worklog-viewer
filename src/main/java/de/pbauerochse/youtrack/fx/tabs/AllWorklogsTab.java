package de.pbauerochse.youtrack.fx.tabs;

import de.pbauerochse.youtrack.domain.TaskWithWorklogs;
import de.pbauerochse.youtrack.domain.WorklogResult;
import de.pbauerochse.youtrack.util.FormattingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Patrick Bauerochse
 * @since 02.07.15
 */
public class AllWorklogsTab extends WorklogTab {

    private static final Logger LOGGER = LoggerFactory.getLogger(AllWorklogsTab.class);

    private Optional<List<TaskWithWorklogs>> resultItemsToDisplay = Optional.empty();

    public AllWorklogsTab() {
        super(FormattingUtil.getFormatted("view.main.tabs.all"));
    }

    @Override
    protected List<TaskWithWorklogs> getDisplayResult(WorklogResult result) {

        if (!resultItemsToDisplay.isPresent() || resultToDisplayChangedSinceLastRender) {
            LOGGER.debug("Extracting TaskWithWorklogs from WorklogResult");

            TaskWithWorklogs summary = new TaskWithWorklogs(true);

            List<TaskWithWorklogs> totalSummary = result.getWorklogSummaryMap()
                    .values().stream()
                    .sorted((o1, o2) -> COLLATOR.compare(o1.getIssue(), o2.getIssue()))
                    .peek(userTaskWorklogs -> {
                        userTaskWorklogs
                                .getWorklogItemList()
                                .stream()
                                .forEach(worklogItem -> summary.getWorklogItemList().add(worklogItem));
                    })
                    .collect(Collectors.toList());

            totalSummary.add(summary);
            resultItemsToDisplay = Optional.of(totalSummary);
        }

        return resultItemsToDisplay.get();
    }
}
