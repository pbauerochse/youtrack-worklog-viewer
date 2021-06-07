package de.pbauerochse.worklogviewer.timereport

/**
 * the type of an [WorkItem]
 */
data class WorkItemType(

    /**
     * Unique ID to distinguish this [WorkItemType]
     * from others. This field contains the
     * technical id (e.g. Database Id from the source system)
     */
    val id: String,

    /**
     * A human readable label describing this type
     */
    val label: String

)