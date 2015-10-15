package de.pbauerochse.worklogviewer.fx.tasks;

import de.pbauerochse.worklogviewer.util.ExceptionUtil;
import de.pbauerochse.worklogviewer.util.FormattingUtil;
import de.pbauerochse.worklogviewer.util.SettingsUtil;
import de.pbauerochse.worklogviewer.youtrack.connector.YouTrackConnector;
import de.pbauerochse.worklogviewer.youtrack.connector.YouTrackConnectorFactory;
import de.pbauerochse.worklogviewer.youtrack.createreport.request.CreateReportRequestEntity;
import de.pbauerochse.worklogviewer.youtrack.createreport.response.ReportDetailsResponse;
import de.pbauerochse.worklogviewer.youtrack.csv.YouTrackCsvReportProcessor;
import de.pbauerochse.worklogviewer.youtrack.domain.WorklogReport;
import javafx.concurrent.Task;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;

/**
 * @author Patrick Bauerochse
 * @since 07.07.15
 */
public class FetchTimereportTask extends Task<WorklogReport> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FetchTimereportTask.class);

    private static final int MAX_REPORT_STATUS_POLLS = 5;

    private final FetchTimereportContext context;

    public FetchTimereportTask(FetchTimereportContext context) {
        updateTitle("FetchWorklogs-Task");
        this.context = context;
    }

    @Override
    protected WorklogReport call() throws Exception {
        SettingsUtil.Settings settings = SettingsUtil.loadSettings();
        YouTrackConnector connector = YouTrackConnectorFactory.getInstance();

        updateProgress(0, 100);
        updateMessage(FormattingUtil.getFormatted("worker.progress.login", settings.getYoutrackUsername()));

        // create report
        CreateReportRequestEntity reportRequestEntity = new CreateReportRequestEntity(context);
        updateMessage(FormattingUtil.getFormatted("worker.progress.creatingreport", FormattingUtil.getFormatted(context.getTimerangeProvider().getReportTimerange().getLabelKey())));
        ReportDetailsResponse reportDetailsResponse = connector.createReport(reportRequestEntity);
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

            YouTrackCsvReportProcessor.processResponse(reportData, result);
        } finally {
            // delete the report again
            updateProgress(90, 100);
            updateMessage(FormattingUtil.getFormatted("worker.progress.deletingreport"));

            connector.deleteReport(reportDetailsResponse.getId());

            updateMessage(FormattingUtil.getFormatted("worker.progress.done"));
            updateProgress(100, 100);
        }

        return result;
    }
}
