package de.pbauerochse.worklogviewer.fx.tasks;

import de.pbauerochse.worklogviewer.util.FormattingUtil;
import de.pbauerochse.worklogviewer.youtrack.YouTrackConnector;
import de.pbauerochse.worklogviewer.youtrack.YouTrackConnectorFactory;
import de.pbauerochse.worklogviewer.youtrack.domain.GroupByCategory;
import javafx.concurrent.Task;

import java.util.List;

/**
 * Created by patrick on 07.07.15.
 */
public class GetGroupByCategoriesTask extends Task<List<GroupByCategory>> {

    public GetGroupByCategoriesTask() {
        updateTitle("GetGroupByCategories-Task");
    }

    @Override
    protected List<GroupByCategory> call() throws Exception {
        YouTrackConnector connector = YouTrackConnectorFactory.getInstance();

        updateProgress(0.5, 1);
        updateMessage(FormattingUtil.getFormatted("worker.progress.categories"));

        List<GroupByCategory> possibleGroupByCategories = connector.getPossibleGroupByCategories();

        updateProgress(1, 1);
        updateMessage(FormattingUtil.getFormatted("worker.progress.done"));

        return possibleGroupByCategories;
    }
}
