package de.pbauerochse.worklogviewer.timereport

/**
 * Contains information about a specific user
 */
open class User(
    /**
     * Unique ID to distinguish this [User]
     * from others. This field contains the
     * technical id (e.g. Database Id from the source system)
     */
    val id : String,

    /**
     * Used in the UI to display this [User]
     * to the user of this application.
     * Preferably the full name of the user.
     */
    val label : String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "User(id='$id', label='$label')"
    }
}