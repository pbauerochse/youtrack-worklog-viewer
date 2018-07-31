package de.pbauerochse.worklogviewer.connector.v2018.domain.grouping

import de.pbauerochse.worklogviewer.connector.v2018.Translations
import de.pbauerochse.worklogviewer.connector.v2018.domain.issue.YouTrackIssue
import de.pbauerochse.worklogviewer.connector.v2018.domain.issue.YouTrackWorklogItem
import de.pbauerochse.worklogviewer.report.Issue

/**
 * Special [Grouping] object to
 * overcome the issue, that the YouTrack provided
 * project GroupingField is not properly translated
 * and therefore can not reliably be used to group
 * [YouTrackWorklogItem]s
 */
object ProjectGrouping : Grouping {

    override val id: String = "PROJECT"
    override fun getLabel(): String = Translations.i18n.get("grouping.project")

    /**
     * Group by project
     */
    override fun getGroupingKey(worklogItem: YouTrackWorklogItem, issue: Issue, youtrackIssue: YouTrackIssue): String? = issue.project

}