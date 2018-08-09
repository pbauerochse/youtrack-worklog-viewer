package de.pbauerochse.worklogviewer.connector.v2018.domain.grouping

import de.pbauerochse.worklogviewer.connector.v2018.Translations
import de.pbauerochse.worklogviewer.connector.v2018.domain.issue.YouTrackIssue
import de.pbauerochse.worklogviewer.connector.v2018.domain.issue.YouTrackWorklogItem
import de.pbauerochse.worklogviewer.report.Issue

/**
 * Groups the [YouTrackWorklogItem]s
 * by the author
 */
object WorkItemAuthorGrouping : Grouping {

    override val id: String = "WORK_AUTHOR"
    override fun getLabel(): String = Translations.i18n.get("grouping.workauthor")
    override fun getGroupingKey(worklogItem: YouTrackWorklogItem, issue: Issue, youtrackIssue: YouTrackIssue): String? = worklogItem.author.login

}