package de.pbauerochse.worklogviewer.fx.tabs;

import de.pbauerochse.worklogviewer.util.FormattingUtil;
import de.pbauerochse.worklogviewer.youtrack.TimeReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Patrick Bauerochse
 * @since 02.07.15
 */
class AllWorklogsTab extends WorklogsTab {

    private static final Logger LOGGER = LoggerFactory.getLogger(AllWorklogsTab.class);

    AllWorklogsTab() {
        super(FormattingUtil.getFormatted("view.main.tabs.all"));
    }

    void update(TimeReport report) {

    }
}
