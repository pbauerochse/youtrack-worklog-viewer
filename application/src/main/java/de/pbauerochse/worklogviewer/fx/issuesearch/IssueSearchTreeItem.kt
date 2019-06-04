package de.pbauerochse.worklogviewer.fx.issuesearch

import de.pbauerochse.worklogviewer.report.Issue

interface IssueSearchTreeItem {
    val label: String
    val issue: Issue?
}

data class NamedIssueList(override val label: String) : IssueSearchTreeItem {
    override val issue: Issue? = null
}

data class IssueTreeItem(override val issue: Issue) : IssueSearchTreeItem {
    override val label: String = issue.fullTitle
}