package de.pbauerochse.worklogviewer.view.grouping

import de.pbauerochse.worklogviewer.report.TimeReport
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted

object GroupingFactory {

    private val FIXED_GROUPINGS = listOf(
        NoopGrouping,
        ProjectGrouping,
        WorklogItemBasedGrouping(getFormatted("grouping.worktype")) { it.workType },
        WorklogItemBasedGrouping(getFormatted("grouping.workauthor")) { it.user.username }
    )

    @JvmStatic
    fun getAvailableGroupings(report: TimeReport): List<Grouping> {
        return FIXED_GROUPINGS + report.issues.asSequence()
            .flatMap { it.fields.asSequence() }
            .distinctBy { it.name }
            .map { FieldBasedGrouping(it) }
    }

}