package de.pbauerochse.worklogviewer.fx.tasks;

import de.pbauerochse.worklogviewer.connector.GroupByParameter;
import de.pbauerochse.worklogviewer.connector.YouTrackConnector;
import de.pbauerochse.worklogviewer.connector.YouTrackConnectorLocator;
import de.pbauerochse.worklogviewer.util.FormattingUtil;
import javafx.concurrent.Task;

import java.util.List;

/**
 * Async task, that fetches the GroupByCategory List from
 * YouTrack
 */
public class GetGroupByCategoriesTask extends Task<List<GroupByParameter>> {

    public GetGroupByCategoriesTask() {
        updateTitle("GetGroupByCategories-Task");
    }

    @Override
    protected List<GroupByParameter> call() {
        YouTrackConnector connector = YouTrackConnectorLocator.getActiveConnector();

        updateProgress(0.5, 1);
        updateMessage(FormattingUtil.getFormatted("worker.progress.categories"));

        List<GroupByParameter> possibleGroupByCategories = connector.getGroupByParameters();

        updateProgress(1, 1);
        updateMessage(FormattingUtil.getFormatted("worker.progress.done"));

        return possibleGroupByCategories;
    }
}