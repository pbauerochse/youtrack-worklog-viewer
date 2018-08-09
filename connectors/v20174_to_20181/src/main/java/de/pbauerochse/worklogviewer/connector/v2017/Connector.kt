package de.pbauerochse.worklogviewer.connector.v2017

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import de.pbauerochse.worklogviewer.connector.GroupByParameter
import de.pbauerochse.worklogviewer.connector.ProgressCallback
import de.pbauerochse.worklogviewer.connector.YouTrackConnector
import de.pbauerochse.worklogviewer.connector.v2017.csv.CsvReportReader
import de.pbauerochse.worklogviewer.connector.v2017.domain.groupby.GroupByTypes
import de.pbauerochse.worklogviewer.connector.v2017.domain.groupby.GroupingField
import de.pbauerochse.worklogviewer.connector.v2017.domain.issuedetails.IssueDetailsResponse
import de.pbauerochse.worklogviewer.connector.v2017.domain.issuedetails.IssueField
import de.pbauerochse.worklogviewer.connector.v2017.domain.report.*
import de.pbauerochse.worklogviewer.connector.v2017.jackson.IssueFieldDeserializer
import de.pbauerochse.worklogviewer.connector.v2017.url.UrlBuilder
import de.pbauerochse.worklogviewer.http.Http
import de.pbauerochse.worklogviewer.http.isValid
import de.pbauerochse.worklogviewer.report.Issue
import de.pbauerochse.worklogviewer.report.TimeReport
import de.pbauerochse.worklogviewer.report.TimeReportParameters
import de.pbauerochse.worklogviewer.toLocalDateTime
import org.apache.http.StatusLine
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.InputStream

/**
 * Connector for YouTrack 2017.4 to 2018.1
 */
class Connector(
    private val urlBuilder: UrlBuilder,
    private val http: Http
) : YouTrackConnector {

    override fun getGroupByParameters(): List<GroupByParameter> {
        val url = urlBuilder.getGroupByParametersUrl()
        LOGGER.debug("Getting GroupByParameters from $url")

        val response = http.get(url)
        if (response.isError) {
            throw IllegalStateException("Fetching GroupByParameters failed: ${response.error}")
        }

        val groupingFields = OBJECT_MAPPER
            .readValue<List<GroupingField>>(response.content!!, object : TypeReference<List<GroupingField>>() {})
            .filter { it.isProcessableFieldGrouping }
            .map { FieldBasedGrouping(it) }

        return CONSTANT_GROUP_BY_PARAMETERS + groupingFields
    }

    override fun getTimeReport(parameters: TimeReportParameters, progressCallback: ProgressCallback): TimeReport {
        return downloadReportCsv(parameters, progressCallback).use {
            progressCallback.setProgress(Translations.i18n.get("report.csv.processing"), 70)
            val csvReportData = CsvReportReader.read(it)

            progressCallback.setProgress(Translations.i18n.get("report.csv.processing"), 80)
            val timeReport = processCsvReport(parameters, csvReportData)

            progressCallback.setProgress(Translations.i18n.get("done"), 100)
            return@use timeReport
        }
    }

    private fun downloadReportCsv(parameters: TimeReportParameters, progressCallback: ProgressCallback): InputStream {
        progressCallback.setProgress(Translations.i18n.get("report.create"), 0)

        var reportDetail = triggerTimeReportCreation(parameters)
        var pollCount = 0

        try {
            progressCallback.setProgress(Translations.i18n.get("report.waiting"), 30)
            while (reportDetail.inProgress && pollCount++ < MAX_POLL_COUNT) {
                waitUntilNextPoll()
                reportDetail = getReportDetail(reportDetail)
            }

            if (reportDetail.inProgress) {
                throw IllegalStateException(Translations.i18n.get("report.toolong", MAX_POLL_COUNT))
            }

            progressCallback.setProgress(Translations.i18n.get("report.downloading", reportDetail.id), 50)
            return downloadReport(reportDetail)
        } finally {
            progressCallback.setProgress(Translations.i18n.get("report.deleting", reportDetail.id), 60)
            deleteReport(reportDetail)
        }
    }

    private fun triggerTimeReportCreation(parameters: TimeReportParameters): ReportDetails {
        val url = urlBuilder.getCreateReportUrl()
        LOGGER.debug("Creating report with $parameters using URL $url")

        val payload = OBJECT_MAPPER.writeValueAsString(CreateReportRequestParameters(parameters))
        val response = http.post(url, StringEntity(payload, ContentType.APPLICATION_JSON))

        if (response.isError) {
            throw IllegalStateException("Creating time report failed: ${response.error}")
        }

        return OBJECT_MAPPER.readValue(response.content, ReportDetails::class.java)
    }

    private fun waitUntilNextPoll() {
        LOGGER.debug("Waiting for report generation to finish")
        Thread.sleep((POLL_INTERVAL_IN_SECONDS * 1000).toLong())
    }

    private fun getReportDetail(reportDetails: ReportDetails): ReportDetails {
        val url = urlBuilder.getReportDetailsUrl(reportDetails.id)
        LOGGER.debug("Fetching report status for report $reportDetails using URL $url")
        val response = http.get(url)

        return if (response.isError) {
            reportDetails
        } else {
            val status = OBJECT_MAPPER.readValue(response.content, ReportStatus::class.java)
            return ReportDetails(reportDetails.id, status)
        }
    }

    private fun downloadReport(report: ReportDetails): InputStream {
        val url = urlBuilder.getDownloadReportCsvUrl(report.id)
        LOGGER.debug("Downloading report $report using URL $url")
        return http.download(url) { response ->
            if (response.statusLine.isValid().not()) {
                throw IllegalStateException("Could not download time report: ${response.statusLine.reasonPhrase}")
            }

            if (mightBeBlankReport(response.statusLine)) {
                ByteArrayInputStream(ByteArray(0))
            } else {
                ByteArrayInputStream(EntityUtils.toByteArray(response.entity))
            }
        }
    }

    private fun deleteReport(report: ReportDetails) {
        val url = urlBuilder.getDeleteReportUrl(report.id)
        LOGGER.debug("Deleting report $report using URL $url")
        http.delete(url)
    }

    private fun processCsvReport(parameters: TimeReportParameters, csvIssues: List<Issue>): TimeReport {
        val issueIdToIssue = csvIssues.associateBy { it.id }

        csvIssues.map { it.id }.chunked(100).forEach {

            val idsJoined = it.joinToString(",") { it.trim() }
            val requestParams = listOf(
                BasicNameValuePair("filter", "issue id: $idsJoined"),
                BasicNameValuePair("with", "id"),
                BasicNameValuePair("with", "resolved"),
                BasicNameValuePair("max", "${it.size}")
            )
            val url = urlBuilder.getIssueDetailsUrl(requestParams)
            LOGGER.debug("Fetching details for ${it.size} Issues using $url")

            val response = http.get(url)

            if (response.isError.not()) {
                OBJECT_MAPPER
                    .readValue(response.content, IssueDetailsResponse::class.java)
                    .issues
                    .filter { it.isResolved }
                    .forEach {
                        val resolutionField = it.fields.find { it.isResolvedField }
                        val resolutionDate = resolutionField?.value?.toLong()?.toLocalDateTime()
                        issueIdToIssue[it.issueId]?.resolutionDate = resolutionDate
                    }
            }
        }

        return TimeReport(parameters, issueIdToIssue.values.sorted())
    }

    /**
     * YouTrack might return status 500 when no time
     * tracking occured for the requested time period
     */
    private fun mightBeBlankReport(statusLine: StatusLine) = statusLine.statusCode == 500

    companion object {
        private val LOGGER = LoggerFactory.getLogger(Connector::class.java)

        private val OBJECT_MAPPER = ObjectMapper()
            .registerModule(SimpleModule("IssueField").addDeserializer(IssueField::class.java, IssueFieldDeserializer()))

        private val CONSTANT_GROUP_BY_PARAMETERS = listOf<GroupByParameter>(
            WorkItemBasedGrouping(GroupByTypes("WORK_TYPE", Translations.i18n.get("grouping.worktype"))),
            WorkItemBasedGrouping(GroupByTypes("WORK_AUTHOR", Translations.i18n.get("grouping.workauthor"))),
            WorkItemBasedGrouping(GroupByTypes("WORK_AUTHOR_AND_DATE", Translations.i18n.get("grouping.workauthoranddate")))
        )
        private const val MAX_POLL_COUNT = 10
        private const val POLL_INTERVAL_IN_SECONDS = 2
    }
}