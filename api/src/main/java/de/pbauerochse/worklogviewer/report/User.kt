package de.pbauerochse.worklogviewer.report

/**
 * A YouTrack user
 */
data class User(
    val username : String,
    val displayName : String
) {

    constructor(username: String) : this(username, username)

}