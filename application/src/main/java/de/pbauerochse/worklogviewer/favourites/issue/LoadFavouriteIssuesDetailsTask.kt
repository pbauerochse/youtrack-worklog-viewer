package de.pbauerochse.worklogviewer.favourites.issue

import de.pbauerochse.worklogviewer.datasource.DataSources
import de.pbauerochse.worklogviewer.favourites.settings.PersistedFavouriteIssue
import de.pbauerochse.worklogviewer.tasks.Progress
import de.pbauerochse.worklogviewer.tasks.WorklogViewerTask
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted

/**
 * Loads the [Issue]s from the [de.pbauerochse.worklogviewer.datasource.TimeTrackingDataSource]
 * that match the id of the given list of [PersistedFavouriteIssue]s
 */
class LoadFavouriteIssuesDetailsTask(private val persistedIssues: List<PersistedFavouriteIssue>): WorklogViewerTask<List<FavouriteIssue>>(getFormatted("favourites.issues.loading")) {

    override fun start(progress: Progress): List<FavouriteIssue> {
        val issueIds = persistedIssues.map { it.id }
        val searchResults = DataSources.activeDataSource!!.loadIssuesByIds(issueIds, progress)
        return searchResults.map {
            FavouriteIssue(it)
        }
    }

}