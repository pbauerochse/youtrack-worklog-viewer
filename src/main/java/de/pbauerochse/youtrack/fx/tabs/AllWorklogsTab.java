package de.pbauerochse.youtrack.fx.tabs;

import de.pbauerochse.youtrack.domain.ReportTimerange;
import de.pbauerochse.youtrack.domain.TaskWithWorklogs;
import de.pbauerochse.youtrack.util.SettingsUtil;

import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Patrick Bauerochse
 * @since 02.07.15
 */
public class AllWorklogsTab extends WorklogTab {

    public AllWorklogsTab(List<TaskWithWorklogs> worklogsList, ReportTimerange timerange, ResourceBundle resourceBundle, SettingsUtil.Settings settings) {
        super(resourceBundle.getString("view.main.tabs.all"), worklogsList, timerange, resourceBundle, settings);
    }

}
