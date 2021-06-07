package de.pbauerochse.worklogviewer.view.grouping

import de.pbauerochse.worklogviewer.timereport.IssueWithWorkItems
import de.pbauerochse.worklogviewer.timereport.view.ReportRow
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted

interface Grouping {
    companion object {
        val UNGROUPED : String = getFormatted("grouping.none")
    }

    val id : String
    val label : String
    fun rows(issues: List<IssueWithWorkItems>) : List<ReportRow>
}