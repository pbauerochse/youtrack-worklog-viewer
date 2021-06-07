package de.pbauerochse.worklogviewer.datasource.api.domain.adapters

import de.pbauerochse.worklogviewer.datasource.api.domain.YouTrackIssueWorkItem
import de.pbauerochse.worklogviewer.timereport.User
import de.pbauerochse.worklogviewer.timereport.WorkItem
import de.pbauerochse.worklogviewer.timereport.WorkItemType
import java.time.ZonedDateTime

class WorkItemAdapter(
    youtrackWorkItem: YouTrackIssueWorkItem,
    override val belongsToCurrentUser: Boolean
): WorkItem {
    override val id: String = youtrackWorkItem.id
    override val owner: User = UserAdapter(youtrackWorkItem.author!!)
    override val workDate: ZonedDateTime = youtrackWorkItem.date
    override val durationInMinutes: Long = youtrackWorkItem.duration.minutes
    override val description: String = youtrackWorkItem.text ?: ""
    override val workType: WorkItemType? = youtrackWorkItem.type?.let { WorkItemType(it.id, it.name ?: it.id) }
}