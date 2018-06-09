package de.pbauerochse.worklogviewer.youtrack.domain

/**
 * A YouTrack project
 */
data class Project(val id: String, val issues: List<Issue>)