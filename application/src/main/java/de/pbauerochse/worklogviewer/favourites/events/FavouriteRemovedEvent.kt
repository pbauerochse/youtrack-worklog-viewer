package de.pbauerochse.worklogviewer.favourites.events

import de.pbauerochse.worklogviewer.favourites.issue.FavouriteIssue
import de.pbauerochse.worklogviewer.favourites.searches.FavouriteSearch

class FavouriteRemovedEvent private constructor(
    val addedIssue: FavouriteIssue?,
    val addedSearch: FavouriteSearch?
) {
    companion object {
        fun forIssue(issue: FavouriteIssue) = FavouriteRemovedEvent(issue, null)
        fun forSearch(search: FavouriteSearch) = FavouriteRemovedEvent(null, search)
    }
}