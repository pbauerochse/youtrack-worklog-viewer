package de.pbauerochse.youtrack.fx.tabs;

import javafx.scene.Node;

/**
 * @author Patrick Bauerochse
 * @since 02.07.15
 */
public class ProjectWorklogTab extends WorklogTab {


    public ProjectWorklogTab(String projectName) {
        super(projectName);
    }

    @Override
    protected Node getStatisticsView() {
        // TODO macht et!
        return super.getStatisticsView();
    }
}
