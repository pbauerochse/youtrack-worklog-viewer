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
        progress.setProgress("favourites.issues.loading", 0.1)
        val issueIds = persistedIssues.associateBy { it.id }
        val searchResults = DataSources.activeDataSource!!.loadIssuesByIds(issueIds.keys, progress)
        progress.setProgress("worker.progress.done", 90.0)
        return searchResults.map {
            FavouriteIssue(it)
        }
    }

}