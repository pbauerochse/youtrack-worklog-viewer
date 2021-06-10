package de.pbauerochse.worklogviewer.search.fx

import de.pbauerochse.worklogviewer.fx.issuesearch.task.SearchIssuesTask
import de.pbauerochse.worklogviewer.tasks.Tasks
import org.slf4j.LoggerFactory

object Search {

    private val logger = LoggerFactory.getLogger(Search::class.java)

    fun issues(query: String, offset: Int = 0, maxResults: Int = 50) {
        Tasks.startTask(SearchIssuesTask(query, offset, maxResults).apply {
            setOnSucceeded {
                logger.info("Found ${value.size} Issues searching for '$query'")
                when {
                    this.isNewSearch -> SearchModel.searchResults.setAll(value)
                    else -> SearchModel.searchResults.addAll(value)
                }
            }
        })
    }

}