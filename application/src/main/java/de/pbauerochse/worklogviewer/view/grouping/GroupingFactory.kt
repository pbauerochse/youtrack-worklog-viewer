package de.pbauerochse.worklogviewer.view.grouping

import de.pbauerochse.worklogviewer.timereport.Field
import de.pbauerochse.worklogviewer.timereport.TimeReport
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import java.text.Collator
import java.util.*

object GroupingFactory {

    private val COLLATOR = Collator.getInstance(Locale.getDefault())
    private val REPORT_GROUP_COMPARATOR = Comparator<Field> { o1, o2 -> COLLATOR.compare(o1.name, o2.name) }

    private val FIXED_GROUPINGS = listOf(
        NoopGrouping,
        ProjectGrouping,
        WorklogItemBasedGrouping("WORKTYPE", getFormatted("grouping.worktype")) { it.workType?.label },
        WorklogItemBasedGrouping("WORKAUTHOR", getFormatted("grouping.workauthor")) { it.owner.label }
    )

    @JvmStatic
    fun getAvailableGroupings(report: TimeReport): List<Grouping> {
        return FIXED_GROUPINGS + report.issues.asSequence()
            .flatMap { it.issue.fields.asSequence() }
            .distinctBy { it.name }
            .sortedWith(REPORT_GROUP_COMPARATOR)
            .map { FieldBasedGrouping(it.name) }
    }

}