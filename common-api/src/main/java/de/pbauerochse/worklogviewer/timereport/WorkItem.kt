package de.pbauerochse.worklogviewer.timereport

import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Represents a single time booking entry
 */
interface WorkItem {

    /**
     * Unique ID to distinguish this [WorkItem]
     * from others. This field contains the
     * technical id (e.g. Database Id from the source system)
     */
    val id: String

    /**
     * The [User] that created this [WorkItem].
     * Must never be `null`
     */
    val owner: User

    /**
     * The date for which this [WorkItem]
     * has been created. Must never be `null`.
     * Please note, that the timezone may not match
     * the timezone of the user. To get the date at
     * the user timezone use `workDateAtLocalZone` instead
     */
    val workDate: ZonedDateTime

    /**
     * The booked time in minutes. Must be `>0` and never
     * be `null`
     */
    val durationInMinutes: Long

    /**
     * A short description of the work performed.
     * May return an empty string, but never `null`
     */
    val description: String

    /**
     * An optional type of work. e.g. "Development" or "Project management".
     */
    val workType: WorkItemType?

    /**
     * Returns `true` if this [WorkItem] has been created
     * by the current user.
     */
    val belongsToCurrentUser: Boolean

    /**
     * Returns the `workDate` at the local Timezone
     */
    val workDateAtLocalZone: ZonedDateTime
        get() = workDate.withZoneSameInstant(ZoneId.systemDefault())

}