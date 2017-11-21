package de.pbauerochse.worklogviewer.youtrack;

import de.pbauerochse.worklogviewer.domain.TimerangeProvider;
import de.pbauerochse.worklogviewer.youtrack.domain.GroupByCategory;
import de.pbauerochse.worklogviewer.youtrack.domain.WorklogReport;

import java.util.Optional;

public interface TimereportContext {

    TimerangeProvider getTimerangeProvider();

    Optional<GroupByCategory> getGroupByCategory();

    Optional<WorklogReport> getResult();

}
