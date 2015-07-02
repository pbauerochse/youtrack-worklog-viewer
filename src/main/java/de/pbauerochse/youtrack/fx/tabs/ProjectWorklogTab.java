package de.pbauerochse.youtrack.fx.tabs;

import de.pbauerochse.youtrack.util.SettingsUtil;
import javafx.scene.Node;

import java.util.ResourceBundle;

/**
 * @author Patrick Bauerochse
 * @since 02.07.15
 */
public class ProjectWorklogTab extends WorklogTab {


    public ProjectWorklogTab(String projectName, ResourceBundle resourceBundle, SettingsUtil.Settings settings) {
        super(projectName, resourceBundle, settings);
    }

    @Override
    protected Node getStatisticsView() {
        // TODO macht et!
        return super.getStatisticsView();
    }
}
