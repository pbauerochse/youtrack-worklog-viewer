package de.pbauerochse.worklogviewer.view

import de.pbauerochse.worklogviewer.report.Issue
import java.time.LocalDate

interface FlatReportRow {
    val label : String
    fun getDurationInMinutes(date : LocalDate) : Long
    val totalDurationInMinutes : Long
}

interface ReportGroup : FlatReportRow {
    val children : List<ReportGroup>
    fun flatten() : List<FlatReportRow>
}

data class GroupReportRow(override val label: String, override val children: List<ReportGroup>) : ReportGroup {

    override fun getDurationInMinutes(date: LocalDate): Long = children.asSequence()
        .map { it.getDurationInMinutes(date) }
        .sum()

    override val totalDurationInMinutes: Long = children.asSequence()
        .map { it.totalDurationInMinutes }
        .sum()

    override fun flatten(): List<FlatReportRow> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

data class IssueReportGroup(private val issue: Issue) : ReportGroup {
    override val label: String = issue.id
    override val children: List<ReportGroup> = emptyList()
    override val totalDurationInMinutes: Long = issue.getTotalTimeInMinutes()

    override fun getDurationInMinutes(date: LocalDate): Long = issue.getTimeInMinutesSpentOn(date)

    override fun flatten(): List<FlatReportRow> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

