package de.pbauerochse.youtrack.csv;

import com.opencsv.CSVReader;
import de.pbauerochse.youtrack.domain.UserTaskWorklogs;
import de.pbauerochse.youtrack.domain.WorklogItem;
import de.pbauerochse.youtrack.domain.WorklogResult;
import de.pbauerochse.youtrack.util.ExceptionUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author Patrick Bauerochse
 * @since 01.04.15
 */
public class YouTrackCsvReportProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(YouTrackCsvReportProcessor.class);

    private static final int DESCRIPTION_COLUMN_INDEX = 0;
    private static final int DATE_COLUMN_INDEX = 1;
    private static final int DURATION_COLUMN_INDEX = 2;
    private static final int TIMEESTIMATION_COLUMN_INDEX = 3;
    private static final int USER_LOGINNAME_COLUMN_INDEX = 4;
    private static final int USER_DISPLAYNAME_COLUMN_INDEX = 5;
    private static final int ISSUE_ID_COLUMN_INDEX = 6;
    private static final int ISSUE_SUMMARY_COLUMN_INDEX = 7;
    private static final int GROUPNAME_SUMMARY_COLUMN_INDEX = 8;
    private static final int WORKLOGTYPE_SUMMARY_COLUMN_INDEX = 9;

    public static void processResponse(InputStream inputStream, WorklogResult result) {
        CSVReader reader = new CSVReader(new InputStreamReader(inputStream), ',', '"', true);

        try {
            String[] line;
            boolean firstLine = true;

            while ((line = reader.readNext()) != null) {
                if (firstLine) {
                    LOGGER.debug("Skipping first line since it is the headline");
                    firstLine = false;
                    continue;
                }

                String taskId = line[ISSUE_ID_COLUMN_INDEX];
                LOGGER.debug("Processing Worklog for task {}", taskId);

                UserTaskWorklogs worklogSummary = result.getWorklog(taskId);
                if (worklogSummary == null) {
                    worklogSummary = new UserTaskWorklogs(false);
                    worklogSummary.setIssue(taskId);
                    worklogSummary.setSummary(line[ISSUE_SUMMARY_COLUMN_INDEX]);
                    result.addWorklogSummary(taskId, worklogSummary);
                }

                String worklogUser = line[USER_DISPLAYNAME_COLUMN_INDEX];
                Long dateAsLong = Long.valueOf(line[DATE_COLUMN_INDEX]);
                Long durationInMinutes = Long.valueOf(line[DURATION_COLUMN_INDEX]);

                WorklogItem worklogItem = new WorklogItem();
                worklogItem.setDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(dateAsLong), ZoneId.systemDefault()).toLocalDate());
                worklogItem.setDurationInMinutes(durationInMinutes);
                worklogItem.setDescription(line[DESCRIPTION_COLUMN_INDEX]);
                worklogItem.setUsername(worklogUser);

                worklogSummary.getWorklogItemList().add(worklogItem);
            }
        } catch (IOException e) {
            throw ExceptionUtil.getRuntimeException("exceptions.report.csvparser.io", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

}
