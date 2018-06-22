package de.pbauerochse.worklogviewer.youtrack.csv

import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReader
import com.opencsv.CSVReaderBuilder
import de.pbauerochse.worklogviewer.toLocalDate
import de.pbauerochse.worklogviewer.trimToNull
import de.pbauerochse.worklogviewer.youtrack.domain.Issue
import de.pbauerochse.worklogviewer.youtrack.domain.Project
import de.pbauerochse.worklogviewer.youtrack.domain.WorklogItem
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Reader for the CSV time report as
 * returned by YouTrack
 */
@Deprecated("")
object CsvReportReader {

    private val LOGGER = LoggerFactory.getLogger(CsvReportReader::class.java)

    private const val DESCRIPTION_COLUMN_INDEX = 0
    private const val DATE_COLUMN_INDEX = 1
    private const val DURATION_COLUMN_INDEX = 2
    private const val TIMEESTIMATION_COLUMN_INDEX = 3
    private const val USER_LOGINNAME_COLUMN_INDEX = 4
    private const val USER_DISPLAYNAME_COLUMN_INDEX = 5
    private const val ISSUE_ID_COLUMN_INDEX = 6
    private const val ISSUE_SUMMARY_COLUMN_INDEX = 7
    private const val GROUPNAME_SUMMARY_COLUMN_INDEX = 8
    private const val WORKLOGTYPE_SUMMARY_COLUMN_INDEX = 9

    @JvmStatic
    fun processResponse(inputStream: InputStream): CsvReportData {

        val issueIdsToIssue = mutableMapOf<String, Issue>()

        getReader(inputStream).use {
            it.forEach {
                val issueId = it[ISSUE_ID_COLUMN_INDEX]
                val issueDescription = it[ISSUE_SUMMARY_COLUMN_INDEX]
                val issueEstimateInMinutes = it[TIMEESTIMATION_COLUMN_INDEX].toLong()
                val issue = issueIdsToIssue.computeIfAbsent(issueId, {id -> Issue(id, issueDescription, issueEstimateInMinutes)})
                issue.worklogItems.add(WorklogItem(
                    issue,
                    it[USER_LOGINNAME_COLUMN_INDEX],
                    it[USER_DISPLAYNAME_COLUMN_INDEX],
                    it[DESCRIPTION_COLUMN_INDEX].trim(),
                    it[DATE_COLUMN_INDEX].toLong().toLocalDate(),
                    it[DURATION_COLUMN_INDEX].toLong(),
                    it[WORKLOGTYPE_SUMMARY_COLUMN_INDEX],
                    it[GROUPNAME_SUMMARY_COLUMN_INDEX].trimToNull()
                ))
            }
        }

        val projects = issueIdsToIssue.values
            .groupBy { it.project }
            .map { Project(it.key, it.value) }

        LOGGER.debug("Processed ${issueIdsToIssue.size} Issues in ${projects.size} Projects")
        return CsvReportData(projects)
    }

    private fun getReader(inputStream: InputStream): CSVReader {
        val parser = CSVParserBuilder()
            .withSeparator(',')
            .withQuoteChar('"')
            .withStrictQuotes(true)
            .build()

        return CSVReaderBuilder(InputStreamReader(inputStream))
            .withCSVParser(parser)
            .withSkipLines(1)
            .build()
    }
}
