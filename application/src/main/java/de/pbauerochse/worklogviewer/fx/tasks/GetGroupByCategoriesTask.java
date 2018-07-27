package de.pbauerochse.worklogviewer.fx.tasks;

import de.pbauerochse.worklogviewer.connector.GroupByParameter;
import de.pbauerochse.worklogviewer.connector.YouTrackConnector;
import de.pbauerochse.worklogviewer.connector.YouTrackConnectorLocator;
import de.pbauerochse.worklogviewer.util.FormattingUtil;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Async task, that fetches the GroupByCategory List from
 * YouTrack
 */
public class GetGroupByCategoriesTask extends Task<List<GroupByParameter>> {

    private static final Logger LOG = LoggerFactory.getLogger(GetGroupByCategoriesTask.class);

    public GetGroupByCategoriesTask() {
        updateTitle("GetGroupByCategories-Task");
    }

    @Override
    protected List<GroupByParameter> call() {
        YouTrackConnector connector = YouTrackConnectorLocator.getActiveConnector();

        List<GroupByParameter> possibleGroupByCategories = new ArrayList<>();

        if (connector != null) {
            updateProgress(0.5, 1);
            updateMessage(FormattingUtil.getFormatted("worker.progress.categories"));

            try {
                possibleGroupByCategories.addAll(connector.getGroupByParameters());
                updateMessage(FormattingUtil.getFormatted("worker.progress.done"));
            } finally {
                updateProgress(1, 1);
            }
        }

        return possibleGroupByCategories;
    }
}
