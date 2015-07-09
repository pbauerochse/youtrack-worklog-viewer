package de.pbauerochse.worklogviewer.youtrack.csv;

import com.opencsv.CSVReader;
import de.pbauerochse.worklogviewer.util.ExceptionUtil;
import de.pbauerochse.worklogviewer.youtrack.domain.TaskWithWorklogs;
import de.pbauerochse.worklogviewer.youtrack.domain.WorklogItem;
import de.pbauerochse.worklogviewer.youtrack.domain.WorklogReport;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Patrick Bauerochse
 * @since 01.04.15
 */
public class YouTrackCsvReportProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(YouTrackCsvReportProcessor.class);

    private static final Pattern PROJECT_ID_PATTERN = Pattern.compile("^(.+)-\\d+$");

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

    public static void processResponse(InputStream inputStream, WorklogReport result) {
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

                TaskWithWorklogs taskWithWorklogs = getTaskWithWorklogs(result, line);
                WorklogItem worklogItem = getWorklogItem(line);
                taskWithWorklogs.getWorklogItemList().add(worklogItem);
            }
        } catch (IOException e) {
            LOGGER.warn("Could not parse csv report", e);
            throw ExceptionUtil.getRuntimeException("exceptions.report.csvparser.io", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    private static TaskWithWorklogs getTaskWithWorklogs(WorklogReport result, String[] csvLine) {
        String taskId = csvLine[ISSUE_ID_COLUMN_INDEX];

        TaskWithWorklogs taskWithWorklogs = result.getWorklog(taskId);
        if (taskWithWorklogs == null) {
            LOGGER.debug("Found new task id {}", taskId);

            taskWithWorklogs = new TaskWithWorklogs();
            taskWithWorklogs.setIssue(taskId);
            taskWithWorklogs.setSummary(csvLine[ISSUE_SUMMARY_COLUMN_INDEX]);

            Matcher matcher = PROJECT_ID_PATTERN.matcher(taskId);
            if (matcher.matches()) {
                taskWithWorklogs.setProject(matcher.group(1));
            }

            String timeEstimationAsString = csvLine[TIMEESTIMATION_COLUMN_INDEX];

            if (StringUtils.isNotBlank(timeEstimationAsString)) {
                try {
                    taskWithWorklogs.setEstimatedWorktimeInMinutes(Long.valueOf(timeEstimationAsString));
                } catch (NumberFormatException e) {
                    LOGGER.warn("Could not parse long from estimated time {}", timeEstimationAsString, e);
                }
            }

            result.addWorklogSummary(taskWithWorklogs);
        }

        return taskWithWorklogs;
    }

    private static WorklogItem getWorklogItem(String[] csvLine) {
        Long dateAsLong = Long.valueOf(csvLine[DATE_COLUMN_INDEX]);
        Long durationInMinutes = Long.valueOf(csvLine[DURATION_COLUMN_INDEX]);

        WorklogItem worklogItem = new WorklogItem();
        worklogItem.setWorkDescription(csvLine[DESCRIPTION_COLUMN_INDEX]);
        worklogItem.setDate(getDate(dateAsLong));
        worklogItem.setDurationInMinutes(durationInMinutes);
        worklogItem.setUsername(csvLine[USER_LOGINNAME_COLUMN_INDEX]);
        worklogItem.setUserDisplayname(csvLine[USER_DISPLAYNAME_COLUMN_INDEX]);
        worklogItem.setWorkType(csvLine[WORKLOGTYPE_SUMMARY_COLUMN_INDEX]);
        worklogItem.setGroup(csvLine[GROUPNAME_SUMMARY_COLUMN_INDEX]);

        return worklogItem;
    }

    private static LocalDate getDate(long dateAsLong) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(dateAsLong), ZoneId.systemDefault()).toLocalDate();
    }

}
