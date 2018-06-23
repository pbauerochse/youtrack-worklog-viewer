package de.pbauerochse.worklogviewer.connector

import de.pbauerochse.worklogviewer.version.Version

/**
 * Represents the version of
 * a targeted YouTrack instance
 */
data class YouTrackVersion(
    val id: String,
    val label: String,
    val minimunSupportedVersion: Version
) : Comparable<YouTrackVersion> {

    override fun compareTo(other: YouTrackVersion): Int = minimunSupportedVersion.compareTo(other.minimunSupportedVersion)
}