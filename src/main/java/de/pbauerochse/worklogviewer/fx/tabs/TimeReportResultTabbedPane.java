package de.pbauerochse.worklogviewer.fx.tabs;

import de.pbauerochse.worklogviewer.settings.SettingsUtil;
import de.pbauerochse.worklogviewer.settings.SettingsViewModel;
import de.pbauerochse.worklogviewer.youtrack.TimeReport;
import de.pbauerochse.worklogviewer.youtrack.domain.Project;
import javafx.scene.control.TabPane;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
        List<Project> projects = timeReport.getData().getProjects().stream()
                .sorted(Comparator.comparing(Project::getId))
                .collect(Collectors.toList());

        int firstProjectTabIndex = settingsViewModel.isShowAllWorklogs() ? 2 : 1;
        for (int i = 0; i < projects.size(); i++) {
            Project project = projects.get(i);
            WorklogsTab tab = getOrCreateProjectTabAtIndex(firstProjectTabIndex + i);
            tab.update(project);
        }

        // TODO remove excess tabs
    }

    @NotNull
    private WorklogsTab getOrCreateProjectTabAtIndex(int tabIndex) {
        if (getTabs().size() <= tabIndex) {
            getTabs().add(tabIndex, new ProjectWorklogTab());
        }
        return (WorklogsTab) getTabs().get(tabIndex);
    }

}
