package de.pbauerochse.worklogviewer.fx.tasks;

import de.pbauerochse.worklogviewer.util.FormattingUtil;
import de.pbauerochse.worklogviewer.youtrack.YouTrackService;
import de.pbauerochse.worklogviewer.youtrack.YouTrackServiceFactory;
import de.pbauerochse.worklogviewer.youtrack.domain.GroupByCategory;
import javafx.concurrent.Task;

import java.util.List;

/**
 * Async task, that fetches the GroupByCategory List from
 * YouTrack
 */
public class GetGroupByCategoriesTask extends Task<List<GroupByCategory>> {

    public GetGroupByCategoriesTask() {
        updateTitle("GetGroupByCategories-Task");
    }

    @Override
    protected List<GroupByCategory> call() {
        YouTrackService youTrackService = YouTrackServiceFactory.INSTANCE.getInstance();

        updateProgress(0.5, 1);
        updateMessage(FormattingUtil.getFormatted("worker.progress.categories"));

        List<GroupByCategory> possibleGroupByCategories = youTrackService.getPossibleGroupByCategories();

        updateProgress(1, 1);
        updateMessage(FormattingUtil.getFormatted("worker.progress.done"));

        return possibleGroupByCategories;
    }
}
