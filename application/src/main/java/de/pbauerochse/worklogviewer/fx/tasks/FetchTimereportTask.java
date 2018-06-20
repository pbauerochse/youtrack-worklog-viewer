package de.pbauerochse.worklogviewer.fx.tasks;

import de.pbauerochse.worklogviewer.youtrack.TimeReport;
import de.pbauerochse.worklogviewer.youtrack.TimeReportParameters;
import de.pbauerochse.worklogviewer.youtrack.YouTrackService;
import de.pbauerochse.worklogviewer.youtrack.YouTrackServiceFactory;
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
        YouTrackService service = YouTrackServiceFactory.getYouTrackService();
        return service.getReport(parameters, this::updateProgress);
    }

    private void updateProgress(String message, int amount) {
        updateProgress(amount, 100);
        updateMessage(message);
    }

}
