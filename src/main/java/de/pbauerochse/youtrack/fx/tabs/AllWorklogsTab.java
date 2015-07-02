package de.pbauerochse.youtrack.fx.tabs;

import de.pbauerochse.youtrack.util.SettingsUtil;

import java.util.ResourceBundle;

/**
 * @author Patrick Bauerochse
 * @since 02.07.15
 */
public class AllWorklogsTab extends WorklogTab {

    public AllWorklogsTab(ResourceBundle resourceBundle, SettingsUtil.Settings settings) {
        super(resourceBundle.getString("view.main.tabs.all"), resourceBundle, settings);
    }

}
