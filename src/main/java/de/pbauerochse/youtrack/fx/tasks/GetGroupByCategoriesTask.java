package de.pbauerochse.youtrack.fx.tasks;

import de.pbauerochse.youtrack.connector.YouTrackConnector;
import javafx.concurrent.Task;

/**
 * Created by patrick on 07.07.15.
 */
public class GetGroupByCategoriesTask extends Task<String> {

    @Override
    protected String call() throws Exception {
        YouTrackConnector connector = YouTrackConnector.getInstance();
        updateMessage("Anmelden");
        updateProgress(0,1);

        connector.login();

        updateProgress(0.5, 1);
        updateMessage("Hole Kategorien");

        connector.getPossibleGroupByCategories();

        updateProgress(1, 1);
        updateMessage("Fertig");

        return "Padde";
    }
}
