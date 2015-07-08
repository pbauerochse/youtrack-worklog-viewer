package de.pbauerochse.youtrack.fx.tasks;

import de.pbauerochse.youtrack.connector.YouTrackConnector;
import de.pbauerochse.youtrack.connector.createreport.request.CreateReportRequestEntity;
import de.pbauerochse.youtrack.connector.createreport.response.ReportDetailsResponse;
import de.pbauerochse.youtrack.csv.YouTrackCsvReportProcessor;
import de.pbauerochse.youtrack.domain.WorklogResult;
import de.pbauerochse.youtrack.util.ExceptionUtil;
import de.pbauerochse.youtrack.util.FormattingUtil;
import de.pbauerochse.youtrack.util.SettingsUtil;
import javafx.concurrent.Task;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;

/**
 * @author Patrick Bauerochse
 * @since 07.07.15
 */
public class FetchTimereportTask extends Task<WorklogResult> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FetchTimereportTask.class);

    private static final int MAX_REPORT_STATUS_POLLS = 5;

    private final FetchTimereportContext context;

    public FetchTimereportTask(FetchTimereportContext context) {
        updateTitle("FetchWorklogs-Task");
        this.context = context;
    }

    @Override
    protected WorklogResult call() throws Exception {
        SettingsUtil.Settings settings = SettingsUtil.loadSettings();
        YouTrackConnector connector = YouTrackConnector.getInstance();

        updateProgress(0, 100);
        updateMessage(FormattingUtil.getFormatted("worker.progress.login", settings.getYoutrackUsername()));

        // login to the youtrack api
        LOGGER.debug("Logging in to YouTrack at {} as {}", settings.getYoutrackUrl(), settings.getYoutrackUsername());
        connector.login();
        updateProgress(10, 100);

        // create report
        CreateReportRequestEntity reportRequestEntity = new CreateReportRequestEntity(context.getTimerangeProvider());
        updateMessage(FormattingUtil.getFormatted("worker.progress.creatingreport", FormattingUtil.getFormatted(context.getTimerangeProvider().getReportTimerange().getLabelKey())));
        ReportDetailsResponse reportDetailsResponse = connector.createReport(reportRequestEntity);
        updateProgress(50, 100);

        // report generation succeeded and is in progress right now
        // giant try block to finally delete the report again even
        // in error cases to prevent polluted user report view
        WorklogResult returnResult = new WorklogResult(context.getTimerangeProvider().getReportTimerange());

        try {
            updateMessage(FormattingUtil.getFormatted("worker.progress.waitingforrecalculation"));

            // poll report status every second
            // until report generation is finished or MAX_REPORT_STATUS_POLLS reached
            int currentRetry = 0;
            while (!StringUtils.equals(ReportDetailsResponse.READY_STATE, reportDetailsResponse.getState()) && currentRetry++ < MAX_REPORT_STATUS_POLLS) {
                Thread.sleep(1000);
                reportDetailsResponse = connector.getReportDetails(reportDetailsResponse.getId());
            }

            if (!StringUtils.equals(ReportDetailsResponse.READY_STATE, reportDetailsResponse.getState())) {
                throw ExceptionUtil.getIllegalStateException("exceptions.main.worker.recalculation", MAX_REPORT_STATUS_POLLS);
            }

            // download the generated report
            updateMessage(FormattingUtil.getFormatted("worker.progress.downloadingreport", reportDetailsResponse.getId()));

            ByteArrayInputStream reportData = connector.downloadReport(reportDetailsResponse.getId());

            updateProgress(80, 100);
            updateMessage(FormattingUtil.getFormatted("worker.progress.processingreport"));

            YouTrackCsvReportProcessor.processResponse(reportData, returnResult);

        } finally {
            // delete the report again
            updateProgress(90, 100);
            updateMessage(FormattingUtil.getFormatted("worker.progress.deletingreport"));

            connector.deleteReport(reportDetailsResponse.getId());

            updateMessage(FormattingUtil.getFormatted("worker.progress.done"));
            updateProgress(100, 100);
        }

        return returnResult;
    }
}
