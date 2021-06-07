package de.pbauerochse.worklogviewer.timereport

/**
 *
 */
open class Project(

    /**
     * Unique ID to distinguish this [Issue]
     * from others. This field contains the
     * technical id (e.g. Database Id from the source system)
     */
    val id: String,

    /**
     * The full name of this [Project]
     */
    val name: String,

    /**
     * A short name / key of this [Project]
     */
    val shortName: String

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Project) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return "Project(id='$id', name='$name', shortName='$shortName')"
    }

}