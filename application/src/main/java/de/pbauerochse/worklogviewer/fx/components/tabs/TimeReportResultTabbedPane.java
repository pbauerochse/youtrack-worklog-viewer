package de.pbauerochse.worklogviewer.fx.components.tabs;

import de.pbauerochse.worklogviewer.report.Issue;
import de.pbauerochse.worklogviewer.report.TimeReport;
import de.pbauerochse.worklogviewer.settings.SettingsUtil;
import de.pbauerochse.worklogviewer.settings.SettingsViewModel;
import de.pbauerochse.worklogviewer.view.grouping.Grouping;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

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
    public void update(@NotNull TimeReport timeReport, @NotNull Grouping grouping) {
        LOGGER.debug("Updating data");
        updateOwnWorklogs(timeReport, grouping);
        updateAllWorklogs(timeReport, grouping);
        updateProjectTabs(timeReport, grouping);
    }

    private void updateOwnWorklogs(@NotNull TimeReport timeReport, @NotNull Grouping grouping) {
        getOwnWorklogsTab().update(timeReport, grouping);
    }

    private OwnWorklogsTab getOwnWorklogsTab() {
        if (getTabs().isEmpty()) {
            LOGGER.debug("Adding OwnWorklogsTab");
            getTabs().add(0, new OwnWorklogsTab());
        }
        return (OwnWorklogsTab) getTabs().get(0);
    }

    private void updateAllWorklogs(TimeReport timeReport, @NotNull Grouping grouping) {
        if (settingsViewModel.getShowAllWorklogsProperty().get()) {
            getAllWorklogsTab().update(timeReport, grouping);
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

    private void updateProjectTabs(@NotNull TimeReport timeReport, @NotNull Grouping grouping) {
        Map<String, List<Issue>> projectToIssues = timeReport.getIssues().stream()
                .collect(groupingBy(Issue::getProject));

        List<String> projectNamesSorted = projectToIssues.keySet().stream().sorted().collect(Collectors.toList());

        int firstProjectTabIndex = settingsViewModel.getShowAllWorklogsProperty().get() ? 2 : 1;
        for (int i = 0; i < projectNamesSorted.size(); i++) {
            String project = projectNamesSorted.get(i);
            List<Issue> sortedIssues = projectToIssues.get(project).stream().sorted().collect(Collectors.toList());
            WorklogsTab tab = getOrCreateProjectTabAtIndex(firstProjectTabIndex + i);
            tab.update(project, sortedIssues, timeReport.getReportParameters(), grouping);
        }

        int numTotalRequiredTabs = firstProjectTabIndex + projectNamesSorted.size();
        for (int i = getTabs().size() - 1; i > numTotalRequiredTabs - 1; i--) {
            Tab tab = getTabs().get(i);
            LOGGER.debug("Removing not needed tab {}", tab.getText());
            getTabs().remove(i);
        }
    }

    @NotNull
    private WorklogsTab getOrCreateProjectTabAtIndex(int tabIndex) {
        if (getTabs().size() <= tabIndex) {
            getTabs().add(tabIndex, new ProjectWorklogTab());
        }
        return (WorklogsTab) getTabs().get(tabIndex);
    }

    public WorklogsTab getCurrentlyVisibleTab() {
        return (WorklogsTab) getSelectionModel().getSelectedItem();
    }
}
