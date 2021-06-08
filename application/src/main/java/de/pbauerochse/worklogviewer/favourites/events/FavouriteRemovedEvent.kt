package de.pbauerochse.worklogviewer.favourites.events

import de.pbauerochse.worklogviewer.favourites.issue.FavouriteIssue
import de.pbauerochse.worklogviewer.favourites.searches.FavouriteSearch

class FavouriteRemovedEvent private constructor(
    val removedIssue: FavouriteIssue?,
    val removedSearch: FavouriteSearch?,
    val currentFavouriteIssues: List<FavouriteIssue>,
    val currentFavouriteSearches: List<FavouriteSearch>
) {
    companion object {
        fun forIssue(issue: FavouriteIssue, issues: List<FavouriteIssue>, searches: List<FavouriteSearch>) = FavouriteRemovedEvent(issue, null, issues, searches)
        fun forSearch(search: FavouriteSearch, issues: List<FavouriteIssue>, searches: List<FavouriteSearch>) = FavouriteRemovedEvent(null, search, issues, searches)
    }
}
