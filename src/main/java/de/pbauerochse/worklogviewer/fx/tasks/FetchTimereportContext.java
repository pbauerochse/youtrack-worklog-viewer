package de.pbauerochse.worklogviewer.fx.tasks;

import de.pbauerochse.worklogviewer.domain.TimerangeProvider;
import de.pbauerochse.worklogviewer.youtrack.TimereportContext;
import de.pbauerochse.worklogviewer.youtrack.domain.GroupByCategory;
import de.pbauerochse.worklogviewer.youtrack.domain.WorklogReport;

import java.util.Optional;

/**
 * @author Patrick Bauerochse
 * @since 07.07.15
 */
public class FetchTimereportContext implements TimereportContext {

    private TimerangeProvider timerangeProvider;
    private GroupByCategory groupByCategory;
    private WorklogReport result;

    public FetchTimereportContext(TimerangeProvider timerangeProvider, GroupByCategory groupByCategory) {
        this.timerangeProvider = timerangeProvider;
        this.groupByCategory = groupByCategory;
    }

    void setResult(WorklogReport result) {
        this.result = result;
    }

    @Override
    public TimerangeProvider getTimerangeProvider() {
        return timerangeProvider;
    }

    @Override
    public Optional<GroupByCategory> getGroupByCategory() {
        if (groupByCategory == null || groupByCategory.isNoSelection()) {
            return Optional.empty();
        }

        return Optional.of(groupByCategory);
    }

    @Override
    public Optional<WorklogReport> getResult() {
        return Optional.ofNullable(result);
    }
}
