package de.pbauerochse.worklogviewer.view

import de.pbauerochse.worklogviewer.timereport.Issue
import de.pbauerochse.worklogviewer.timereport.IssueWithWorkItems
import de.pbauerochse.worklogviewer.timereport.TimeReportParameters
import de.pbauerochse.worklogviewer.timereport.view.ReportRow
import de.pbauerochse.worklogviewer.timereport.view.ReportView
import de.pbauerochse.worklogviewer.view.grouping.Grouping
import de.pbauerochse.worklogviewer.view.grouping.Grouping.Companion.UNGROUPED
import de.pbauerochse.worklogviewer.view.grouping.NoopGrouping
import org.slf4j.LoggerFactory
import java.text.Collator
import java.util.*

/**
 * Converts a list of [Issue]s to a [ReportView].
 * A view is the denormalized representation of the data provided in the TimeReport
 * which is suitable to be displayed in a tabled manner (Excel Export / TableView)
 */
object ReportViewFactory {

    private val LOGGER = LoggerFactory.getLogger(ReportViewFactory::class.java)

    private val COLLATOR = Collator.getInstance(Locale.getDefault())
    private val REPORT_ROW_COMPARATOR = Comparator<ReportRow> { o1, o2 ->
        val o1Label = o1.label
        val o2Label = o2.label

        // Ungrouped items always last
        return@Comparator if (UNGROUPED == o1Label && UNGROUPED != o2Label) {
            1
        } else if (UNGROUPED != o1Label && UNGROUPED == o2Label) {
            -1
        } else {
            COLLATOR.compare(o1.label, o2.label)
        }
    }

    fun convert(issues: List<IssueWithWorkItems>, reportParameters: TimeReportParameters, grouping: Grouping = NoopGrouping): ReportView {
        LOGGER.debug("Converting ${issues.size} Issues for ${reportParameters.timerange} with grouping $grouping to ReportView")
        val groups = grouping.rows(issues).sortedWith(REPORT_ROW_COMPARATOR)
        val summaryReportRow = SummaryReportRow(issues)
        return ReportView(groups + summaryReportRow, issues, reportParameters)
    }

}