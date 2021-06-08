package de.pbauerochse.worklogviewer.fx.tasks;

import de.pbauerochse.worklogviewer.datasource.TimeTrackingDataSource;
import de.pbauerochse.worklogviewer.tasks.Progress;
import de.pbauerochse.worklogviewer.tasks.WorklogViewerTask;
import de.pbauerochse.worklogviewer.timereport.TimeReport;
import de.pbauerochse.worklogviewer.timereport.TimeReportParameters;
import org.jetbrains.annotations.NotNull;

import static de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted;

/**
 * Task that loads the TimeReport
 * using the YouTrackConnector
 */
public class FetchTimereportTask extends WorklogViewerTask<TimeReport> {

    private final TimeTrackingDataSource connector;
    private final TimeReportParameters parameters;

    public FetchTimereportTask(@NotNull TimeTrackingDataSource connector, @NotNull TimeReportParameters parameters) {
        super(getFormatted("task.fetchworklogs"));
        this.connector = connector;
        this.parameters = parameters;
    }

    @Override
    public TimeReport start(@NotNull Progress progress) {
        return connector.getTimeReport(parameters, progress);
    }

}
