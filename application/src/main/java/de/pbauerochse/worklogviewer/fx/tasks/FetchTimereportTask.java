package de.pbauerochse.worklogviewer.fx.tasks;

import de.pbauerochse.worklogviewer.connector.ProgressCallback;
import de.pbauerochse.worklogviewer.connector.YouTrackConnector;
import de.pbauerochse.worklogviewer.connector.YouTrackConnectorLocator;
import de.pbauerochse.worklogviewer.report.TimeReport;
import de.pbauerochse.worklogviewer.report.TimeReportParameters;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted;

/**
 * Task that loads the TimeReport
 * using the YouTrackConnector
 */
public class FetchTimereportTask extends WorklogViewerTask<TimeReport> {

    private final TimeReportParameters parameters;

    public FetchTimereportTask(@NotNull TimeReportParameters parameters) {
        this.parameters = parameters;
    }

    @NotNull
    @Override
    public String getLabel() {
        return getFormatted("task.fetchworklogs");
    }

    @Override
    public TimeReport start(@NotNull ProgressCallback progressCallback) {
        YouTrackConnector service = YouTrackConnectorLocator.getActiveConnector();
        return Objects.requireNonNull(service).getTimeReport(parameters, progressCallback);
    }

}
