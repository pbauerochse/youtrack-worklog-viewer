package de.pbauerochse.worklogviewer.favourites.issue

import de.pbauerochse.worklogviewer.timereport.Issue

data class FavouriteIssue(
    /**
     * The freshly loaded [Issue]
     */
    val issue: Issue
)