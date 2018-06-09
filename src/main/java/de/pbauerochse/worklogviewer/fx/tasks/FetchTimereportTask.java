package de.pbauerochse.worklogviewer.fx.tasks;

import de.pbauerochse.worklogviewer.youtrack.TimeReport;
import de.pbauerochse.worklogviewer.youtrack.TimeReportParameters;
import de.pbauerochse.worklogviewer.youtrack.YouTrackService;
import de.pbauerochse.worklogviewer.youtrack.YouTrackServiceFactory;
import javafx.concurrent.Task;
import org.jetbrains.annotations.NotNull;

import static de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted;

/**
 * Task that loads the TimeReport
 * using the YouTrackService
 */
public class FetchTimereportTask extends Task<TimeReport> {

    private final TimeReportParameters parameters;

    public FetchTimereportTask(@NotNull TimeReportParameters parameters) {
        updateTitle("FetchTimereport-Task");
        this.parameters = parameters;
    }

    @Override
    protected TimeReport call() {
        YouTrackService service = YouTrackServiceFactory.getInstance();

        // create report
        String timerangeDisplayLabel = getFormatted(parameters.getTimerangeProvider().getReportTimerange().getLabelKey());
        updateProgress(getFormatted("worker.progress.creatingreport", timerangeDisplayLabel), 0);

        TimeReport timeReport = service.getReport(parameters, this::updateProgress);
        updateProgress(getFormatted("worker.progress.done"), 100);

//        // report generation succeeded and is in progress right now
//        // giant try block to finally delete the report again even
//        // in error cases to prevent polluted user report view
//        WorklogReport result = new WorklogReport();
//        parameters.setResult(result);
//
//        try {
//            updateMessage(FormattingUtil.getFormatted("worker.progress.waitingforrecalculation"));
//
//            // poll report status every second
//            // until report generation is finished or MAX_REPORT_STATUS_POLLS reached
//            int currentRetry = 0;
//            while (timeReport.isRecomputing() && currentRetry++ < MAX_REPORT_STATUS_POLLS) {
//                Thread.sleep(1000);
//                timeReport = service.getReportDetails(timeReport.getReportId());
//            }
//
//            if (timeReport.isRecomputing()) {
//                throw ExceptionUtil.getIllegalStateException("exceptions.main.worker.recalculation", MAX_REPORT_STATUS_POLLS);
//            }
//
//            // download the generated report
//            updateMessage(FormattingUtil.getFormatted("worker.progress.downloadingreport", timeReport.getReportId()));
//
//            Optional.ofNullable(service.downloadReport(timeReport.getReportId())).ifPresent(reportData -> {
//                updateProgress(80, 100);
//                updateMessage(FormattingUtil.getFormatted("worker.progress.processingreport"));
//
//                CsvReportReader.processResponse(reportData, result);
//
//                // fetch issue details
//                service.fetchTaskDetails(result);
//            });
//        } finally {
//            // delete the report again
//            updateProgress(90, 100);
//            updateMessage(FormattingUtil.getFormatted("worker.progress.deletingreport"));
//
//            service.deleteReport(timeReport.getReportId());
//
//            updateMessage(FormattingUtil.getFormatted("worker.progress.done"));
//            updateProgress(100, 100);
//        }

        return timeReport;
    }

    private void updateProgress(String message, int amount) {
        // TODO implement
    }

}
