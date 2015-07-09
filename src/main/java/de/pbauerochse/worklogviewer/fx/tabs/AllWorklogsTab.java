package de.pbauerochse.worklogviewer.fx.tabs;

import de.pbauerochse.worklogviewer.util.FormattingUtil;
import de.pbauerochse.worklogviewer.youtrack.domain.TaskWithWorklogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Patrick Bauerochse
 * @since 02.07.15
 */
public class AllWorklogsTab extends WorklogTab {

    private static final Logger LOGGER = LoggerFactory.getLogger(AllWorklogsTab.class);

    public AllWorklogsTab() {
        super(FormattingUtil.getFormatted("view.main.tabs.all"));
    }

    @Override
    protected List<TaskWithWorklogs> getFilteredList(List<TaskWithWorklogs> tasks) {
        // no filtering neccessary since we display all
        return tasks;
    }
}
