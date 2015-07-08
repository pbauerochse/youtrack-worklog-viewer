package de.pbauerochse.youtrack.fx.tasks;

import de.pbauerochse.youtrack.domain.GroupByCategory;
import de.pbauerochse.youtrack.domain.TimerangeProvider;

/**
 * @author Patrick Bauerochse
 * @since 07.07.15
 */
public class FetchTimereportContext {

    private TimerangeProvider timerangeProvider;
    private GroupByCategory groupByCategory;

    public FetchTimereportContext(TimerangeProvider timerangeProvider, GroupByCategory groupByCategory) {
        this.timerangeProvider = timerangeProvider;
        this.groupByCategory = groupByCategory;
    }

    public TimerangeProvider getTimerangeProvider() {
        return timerangeProvider;
    }

    public GroupByCategory getGroupByCategory() {
        return groupByCategory;
    }
}
