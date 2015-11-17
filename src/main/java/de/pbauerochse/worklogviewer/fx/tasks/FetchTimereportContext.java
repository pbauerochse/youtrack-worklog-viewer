package de.pbauerochse.worklogviewer.fx.tasks;

import de.pbauerochse.worklogviewer.youtrack.domain.GroupByCategory;
import de.pbauerochse.worklogviewer.domain.TimerangeProvider;
import de.pbauerochse.worklogviewer.youtrack.domain.WorklogReport;

import java.util.Optional;

/**
 * @author Patrick Bauerochse
 * @since 07.07.15
 */
public class FetchTimereportContext {

    private TimerangeProvider timerangeProvider;
    private Optional<GroupByCategory> groupByCategory;
    private Optional<WorklogReport> result = Optional.empty();

    public FetchTimereportContext(TimerangeProvider timerangeProvider, Optional<GroupByCategory> groupByCategory) {
        this.timerangeProvider = timerangeProvider;
        this.groupByCategory = groupByCategory;
    }

    public TimerangeProvider getTimerangeProvider() {
        return timerangeProvider;
    }

    public Optional<GroupByCategory> getGroupByCategory() {
        return groupByCategory;
    }

    public void setResult(WorklogReport result) {
        this.result = Optional.of(result);
    }

    public Optional<WorklogReport> getResult() {
        return result;
    }
}
