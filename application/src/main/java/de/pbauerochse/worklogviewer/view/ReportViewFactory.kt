package de.pbauerochse.worklogviewer.view

import de.pbauerochse.worklogviewer.report.Issue
import de.pbauerochse.worklogviewer.report.TimeReportParameters
import de.pbauerochse.worklogviewer.view.grouping.Grouping
import de.pbauerochse.worklogviewer.view.grouping.NoopGrouping
import org.slf4j.LoggerFactory

/**
 * Converts a list of [Issue]s to a [ReportView].
 * A view is the denormalized representation of the data provided in the TimeReport
 * which is suitable to be displayed in a tabled manner (Excel Export / TableView)
 */
object ReportViewFactory {

    private val LOGGER = LoggerFactory.getLogger(ReportViewFactory::class.java)

    fun convert(issues: List<Issue>, reportParameters: TimeReportParameters, grouping: Grouping = NoopGrouping): ReportView {
        LOGGER.info("Converting ${issues.size} Issues for ${reportParameters.timerange} with grouping $grouping to ReportView")
        val groups = grouping.group(issues)
        return ReportView(groups, issues, reportParameters)
    }

}