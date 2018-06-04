package de.pbauerochse.worklogviewer.fx.tasks;

import de.pbauerochse.worklogviewer.settings.Settings;
import de.pbauerochse.worklogviewer.settings.SettingsUtil;
import de.pbauerochse.worklogviewer.util.ExceptionUtil;
import de.pbauerochse.worklogviewer.util.FormattingUtil;
import de.pbauerochse.worklogviewer.youtrack.ReportDetails;
import de.pbauerochse.worklogviewer.youtrack.YouTrackService;
import de.pbauerochse.worklogviewer.youtrack.YouTrackServiceFactory;
import de.pbauerochse.worklogviewer.youtrack.csv.YouTrackCsvReportProcessor;
import de.pbauerochse.worklogviewer.youtrack.domain.WorklogReport;
import javafx.concurrent.Task;

import java.util.Optional;

/**
 * @author Patrick Bauerochse
 * @since 07.07.15
 */
public class FetchTimereportTask extends Task<WorklogReport> {

    private static final int MAX_REPORT_STATUS_POLLS = 5;

    private final FetchTimereportContext context;

    public FetchTimereportTask(FetchTimereportContext context) {
        updateTitle("FetchTimereport-Task");
        this.context = context;
    }

    @Override
    protected WorklogReport call() throws Exception {
        Settings settings = SettingsUtil.getSettings();
        YouTrackService connector = YouTrackServiceFactory.getInstance();

        updateProgress(0, 100);
        updateMessage(FormattingUtil.getFormatted("worker.progress.login", settings.getYoutrackUsername()));

        // create report
        updateMessage(FormattingUtil.getFormatted("worker.progress.creatingreport", FormattingUtil.getFormatted(context.getTimerangeProvider().getReportTimerange().getLabelKey())));
        ReportDetails reportDetails = connector.createReport(context);
        updateProgress(50, 100);

        // report generation succeeded and is in progress right now
        // giant try block to finally delete the report again even
        // in error cases to prevent polluted user report view
        WorklogReport result = new WorklogReport();
        context.setResult(result);

        try {
            updateMessage(FormattingUtil.getFormatted("worker.progress.waitingforrecalculation"));

            // poll report status every second
            // until report generation is finished or MAX_REPORT_STATUS_POLLS reached
            int currentRetry = 0;
            while (reportDetails.isRecomputing() && currentRetry++ < MAX_REPORT_STATUS_POLLS) {
                Thread.sleep(1000);
                reportDetails = connector.getReportDetails(reportDetails.getReportId());
            }

            if (reportDetails.isRecomputing()) {
                throw ExceptionUtil.getIllegalStateException("exceptions.main.worker.recalculation", MAX_REPORT_STATUS_POLLS);
            }

            // download the generated report
            updateMessage(FormattingUtil.getFormatted("worker.progress.downloadingreport", reportDetails.getReportId()));

            Optional.ofNullable(connector.downloadReport(reportDetails.getReportId())).ifPresent(reportData -> {
                updateProgress(80, 100);
                updateMessage(FormattingUtil.getFormatted("worker.progress.processingreport"));

                YouTrackCsvReportProcessor.processResponse(reportData, result);

                // fetch issue details
                connector.fetchTaskDetails(result);
            });
        } finally {
            // delete the report again
            updateProgress(90, 100);
            updateMessage(FormattingUtil.getFormatted("worker.progress.deletingreport"));

            connector.deleteReport(reportDetails.getReportId());

            updateMessage(FormattingUtil.getFormatted("worker.progress.done"));
            updateProgress(100, 100);
        }

        return result;
    }
}
