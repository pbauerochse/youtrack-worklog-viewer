package de.pbauerochse.worklogviewer.view.grouping

import de.pbauerochse.worklogviewer.report.Issue
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import de.pbauerochse.worklogviewer.view.ReportGroup

interface Grouping {
    companion object {
        val UNGROUPED = getFormatted("grouping.none")
    }

    val label : String
    fun group(issues: List<Issue>) : List<ReportGroup>
}