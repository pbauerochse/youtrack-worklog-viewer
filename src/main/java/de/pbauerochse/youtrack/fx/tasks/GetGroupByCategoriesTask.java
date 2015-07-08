package de.pbauerochse.youtrack.fx.tasks;

import de.pbauerochse.youtrack.connector.YouTrackConnector;
import de.pbauerochse.youtrack.domain.GroupByCategory;
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
        YouTrackConnector connector = YouTrackConnector.getInstance();
        updateMessage("Anmelden");
        updateProgress(0, 1);

        connector.login();

        updateProgress(0.5, 1);
        updateMessage("Hole Kategorien");

        List<GroupByCategory> possibleGroupByCategories = connector.getPossibleGroupByCategories();

        updateProgress(1, 1);
        updateMessage("Fertig");

        return possibleGroupByCategories;
    }
}
