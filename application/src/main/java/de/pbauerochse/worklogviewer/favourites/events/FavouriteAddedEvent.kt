package de.pbauerochse.worklogviewer.favourites.events

import de.pbauerochse.worklogviewer.favourites.issue.FavouriteIssue
import de.pbauerochse.worklogviewer.favourites.searches.FavouriteSearch

class FavouriteAddedEvent private constructor(
    val addedIssue: FavouriteIssue?,
    val addedSearch: FavouriteSearch?
) {
    companion object {
        fun forIssue(issue: FavouriteIssue) = FavouriteAddedEvent(issue, null)
        fun forSearch(search: FavouriteSearch) = FavouriteAddedEvent(null, search)
    }
}
