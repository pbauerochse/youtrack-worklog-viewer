package de.pbauerochse.worklogviewer.fx.tasks;

import de.pbauerochse.worklogviewer.connector.YouTrackConnector;
import de.pbauerochse.worklogviewer.connector.YouTrackConnectorLocator;
import de.pbauerochse.worklogviewer.report.TimeReport;
import de.pbauerochse.worklogviewer.report.TimeReportParameters;
import javafx.concurrent.Task;
import org.jetbrains.annotations.NotNull;

/**
 * Task that loads the TimeReport
 * using the YouTrackConnector
 */
public class FetchTimereportTask extends Task<TimeReport> {

    private final TimeReportParameters parameters;

    public FetchTimereportTask(@NotNull TimeReportParameters parameters) {
        updateTitle("FetchTimereport-Task");
        this.parameters = parameters;
    }

    @Override
    protected TimeReport call() {
        YouTrackConnector service = YouTrackConnectorLocator.getActiveConnector();
        return service.getTimeReport(parameters, this::updateProgress);
    }

    private void updateProgress(String message, int amount) {
        updateProgress(amount, 100);
        updateMessage(message);
    }

}
