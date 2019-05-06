package de.pbauerochse.worklogviewer.fx.tasks;

import de.pbauerochse.worklogviewer.connector.GroupByParameter;
import de.pbauerochse.worklogviewer.connector.YouTrackConnector;
import de.pbauerochse.worklogviewer.connector.YouTrackConnectorLocator;
import de.pbauerochse.worklogviewer.tasks.Progress;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted;

/**
 * Async task, that fetches the GroupByCategory List from
 * YouTrack
 */
public class GetGroupByCategoriesTask extends WorklogViewerTask<List<GroupByParameter>> {

    private static final Logger LOG = LoggerFactory.getLogger(GetGroupByCategoriesTask.class);

    public GetGroupByCategoriesTask() {
        super(getFormatted("task.groupby"));
    }

    @Override
    public List<GroupByParameter> start(@NotNull Progress progress) {
        YouTrackConnector connector = YouTrackConnectorLocator.getActiveConnector();
        List<GroupByParameter> possibleGroupByCategories = new ArrayList<>();

        LOG.info("Fetching GroupByParameters from Connector {}", connector);

        if (connector != null) {
            progress.setProgress(getFormatted("worker.progress.categories"), 50);

            try {
                possibleGroupByCategories.addAll(connector.getGroupByParameters());
            } finally {
                progress.setProgress(getFormatted("worker.progress.done"), 100);
            }
        }

        return possibleGroupByCategories;
    }
}
