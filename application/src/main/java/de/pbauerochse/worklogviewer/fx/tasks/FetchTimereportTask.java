package de.pbauerochse.worklogviewer.fx.tasks;

import de.pbauerochse.worklogviewer.connector.YouTrackConnector;
import de.pbauerochse.worklogviewer.report.TimeReport;
import de.pbauerochse.worklogviewer.report.TimeReportParameters;
import de.pbauerochse.worklogviewer.tasks.Progress;
import org.jetbrains.annotations.NotNull;

import static de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted;

/**
 * Task that loads the TimeReport
 * using the YouTrackConnector
 */
public class FetchTimereportTask extends WorklogViewerTask<TimeReport> {

    private final YouTrackConnector connector;
    private final TimeReportParameters parameters;

    public FetchTimereportTask(@NotNull YouTrackConnector connector, @NotNull TimeReportParameters parameters) {
        super(getFormatted("task.fetchworklogs"), true);
        this.connector = connector;
        this.parameters = parameters;
    }

    @Override
    public TimeReport start(@NotNull Progress progress) {
        return connector.getTimeReport(parameters, progress);
    }

}
