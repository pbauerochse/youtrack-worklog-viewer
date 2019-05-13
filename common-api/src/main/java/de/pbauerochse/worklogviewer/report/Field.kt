package de.pbauerochse.worklogviewer.report

/**
 * A custom field on an issue
 */
data class Field(

    /**
     * The name of the field
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