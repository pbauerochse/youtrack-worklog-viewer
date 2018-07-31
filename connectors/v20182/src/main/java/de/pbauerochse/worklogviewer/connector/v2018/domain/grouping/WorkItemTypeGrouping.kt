package de.pbauerochse.worklogviewer.connector.v2018.domain.grouping

import de.pbauerochse.worklogviewer.connector.v2018.Translations
import de.pbauerochse.worklogviewer.connector.v2018.domain.issue.YouTrackIssue
import de.pbauerochse.worklogviewer.connector.v2018.domain.issue.YouTrackWorklogItem
import de.pbauerochse.worklogviewer.report.Issue

/**
 * Groups [YouTrackWorklogItem]s by the type
 * of the performed work
 */
object WorkItemTypeGrouping : Grouping {

    override val id: String = "WORK_TYPE"
    override fun getLabel(): String = Translations.i18n.get("grouping.worktype")
    override fun getGroupingKey(worklogItem: YouTrackWorklogItem, issue: Issue, youtrackIssue: YouTrackIssue): String? = worklogItem.worktype?.name

}