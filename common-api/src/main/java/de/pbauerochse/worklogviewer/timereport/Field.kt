package de.pbauerochse.worklogviewer.timereport

/**
 * A field defining a certain property on an [Issue]
 */
data class Field(

    /**
     * The name of the property
     */
    val name: String,

    /**
     * The values of this field. May be empty in case there
     * is no value set in YouTrack.
     *
     * In YouTrack, there a single value fields, and multi-value fields.
     * Both will be store with this List.
     */
    val value: List<String>
)