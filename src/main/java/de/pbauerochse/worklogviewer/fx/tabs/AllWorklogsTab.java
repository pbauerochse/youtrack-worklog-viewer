package de.pbauerochse.worklogviewer.fx.tabs;

import de.pbauerochse.worklogviewer.util.FormattingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Patrick Bauerochse
 * @since 02.07.15
 */
public class AllWorklogsTab extends WorklogsTab {

    private static final Logger LOGGER = LoggerFactory.getLogger(AllWorklogsTab.class);

    public AllWorklogsTab() {
        super(FormattingUtil.getFormatted("view.main.tabs.all"));
    }

}
