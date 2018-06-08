package de.pbauerochse.worklogviewer.fx.tabs;

import de.pbauerochse.worklogviewer.settings.SettingsUtil;
import de.pbauerochse.worklogviewer.settings.SettingsViewModel;
import de.pbauerochse.worklogviewer.youtrack.ProjectSpecificWorklogs;
import de.pbauerochse.worklogviewer.youtrack.TimeReport;
import javafx.scene.control.TabPane;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Extension of the TabPane that provides
 * additional methods to render the data
 * contained in a TimeReport
 */
public class TimeReportResultTabbedPane extends TabPane {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeReportResultTabbedPane.class);

    private final SettingsViewModel settingsViewModel = SettingsUtil.getSettingsViewModel();

    /**
     * Updates this Pane with the given data
     */
    public void update(@NotNull TimeReport timeReport) {
        LOGGER.debug("Updating data");
        updateOwnWorklogs(timeReport);
        updateAllWorklogs(timeReport);
        updateProjectTabs(timeReport);
        updateStatistics(timeReport);
    }

    private void updateOwnWorklogs(@NotNull TimeReport timeReport) {
        getOwnWorklogsTab().update(timeReport);
    }

    private OwnWorklogsTab getOwnWorklogsTab() {
        if (getTabs().isEmpty()) {
            LOGGER.debug("Adding OwnWorklogsTab");
            getTabs().add(0, new OwnWorklogsTab());
        }
        return (OwnWorklogsTab) getTabs().get(0);
    }

    private void updateAllWorklogs(TimeReport timeReport) {
        if (settingsViewModel.isShowAllWorklogs()) {
            getAllWorklogsTab().update(timeReport);
        } else {
            removeAllWorklogsTab();
        }
    }

    private AllWorklogsTab getAllWorklogsTab() {
        if (getTabs().size() < 2 || !(getTabs().get(1) instanceof AllWorklogsTab)) {
            LOGGER.debug("Adding AllWorklogsTab");
            getTabs().add(1, new AllWorklogsTab());
        }

        return (AllWorklogsTab) getTabs().get(1);
    }

    private void removeAllWorklogsTab() {
        if (getTabs().get(1) instanceof AllWorklogsTab) {
            LOGGER.debug("Removing AllWorklogsTab");
            getTabs().remove(1);
        }
    }

    private void updateProjectTabs(TimeReport timeReport) {
        List<ProjectSpecificWorklogs> projectSpecificWorklogs = timeReport.getProjectSpecificWorklogs();

        int startIndex = settingsViewModel.isShowAllWorklogs() ? 2 : 1;
        int endIndex = startIndex + projectSpecificWorklogs.size();
        int excessTabs = getTabs().size() - endIndex;

        for (int i = startIndex; i < endIndex; i++) {
            ProjectSpecificWorklogs projectWorklogs = projectSpecificWorklogs.get(i);
//            getOrCreateProjectTabAtIndex(i).update(projectWorklogs);
        }
    }

    private WorklogTab getOrCreateProjectTabAtIndex(int tabIndex) {
        return null;
    }

    private void updateStatistics(TimeReport timeReport) {
        // TODO update depending on selected tab but only of statistics enabled
    }

}
