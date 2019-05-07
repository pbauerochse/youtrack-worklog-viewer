package de.pbauerochse.worklogviewer.connector.v2017.csv

import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReader
import com.opencsv.CSVReaderBuilder
import de.pbauerochse.worklogviewer.report.Issue
import de.pbauerochse.worklogviewer.report.User
import de.pbauerochse.worklogviewer.report.WorklogItem
import de.pbauerochse.worklogviewer.toLocalDate
import de.pbauerochse.worklogviewer.trimToNull
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Processes the downloaded
 * time report in csv format
 */
object CsvReportReader {

    private val LOGGER = LoggerFactory.getLogger(CsvReportReader::class.java)

    private const val DESCRIPTION_COLUMN_INDEX = 0
    private const val DATE_COLUMN_INDEX = 1
    private const val DURATION_COLUMN_INDEX = 2
    private const val USER_LOGINNAME_COLUMN_INDEX = 4
    private const val USER_DISPLAYNAME_COLUMN_INDEX = 5
    private const val ISSUE_ID_COLUMN_INDEX = 6
    private const val ISSUE_SUMMARY_COLUMN_INDEX = 7
    private const val GROUPNAME_SUMMARY_COLUMN_INDEX = 8
    private const val WORKLOGTYPE_SUMMARY_COLUMN_INDEX = 9

    fun read(csvInputStream : InputStream) : List<Issue> {
        LOGGER.debug("Reading CSV content")

        val issueIdToIssue = mutableMapOf<String, Issue>()

        getReader(csvInputStream).use {
            it.forEach {
                val issueId = it[ISSUE_ID_COLUMN_INDEX]
                val issueDescription = it[ISSUE_SUMMARY_COLUMN_INDEX]
                val issue = issueIdToIssue.computeIfAbsent(issueId) { id -> Issue(id, issueDescription, null, emptyList()) }
                val user = User(it[USER_LOGINNAME_COLUMN_INDEX], it[USER_DISPLAYNAME_COLUMN_INDEX])
                issue.worklogItems.add(
                    WorklogItem(
                    issue,
                    user,
                    it[DATE_COLUMN_INDEX].toLong().toLocalDate(),
                    it[DURATION_COLUMN_INDEX].toLong(),
                    it[DESCRIPTION_COLUMN_INDEX]?.trim(),
                    it[WORKLOGTYPE_SUMMARY_COLUMN_INDEX],
                    it[GROUPNAME_SUMMARY_COLUMN_INDEX]?.trimToNull()
                ))
            }
        }

        return issueIdToIssue.values.sorted()
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
