package de.pbauerochse.worklogviewer.view.grouping

import de.pbauerochse.worklogviewer.report.Issue
import de.pbauerochse.worklogviewer.report.view.ReportRow
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted

interface Grouping {
    companion object {
        val UNGROUPED : String = getFormatted("grouping.none")
    }

    val id : String
    val label : String
    fun rows(issues: List<Issue>) : List<ReportRow>
}