package de.pbauerochse.worklogviewer.details

import de.pbauerochse.worklogviewer.details.events.CloseIssueDetailsRequestEvent
import de.pbauerochse.worklogviewer.details.events.ShowIssueDetailsRequestEvent
import de.pbauerochse.worklogviewer.events.EventBus
import de.pbauerochse.worklogviewer.events.Subscribe
import de.pbauerochse.worklogviewer.timereport.Issue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.slf4j.LoggerFactory

object IssueDetailsModel {

    init {
        EventBus.subscribe(this)
    }

    private val LOGGER = LoggerFactory.getLogger(IssueDetailsPane::class.java)

    val issuesForDetailsPanel: ObservableList<Issue> = FXCollections.observableArrayList()

    @Subscribe
    fun onOpenIssueDetailsRequest(event: ShowIssueDetailsRequestEvent) {
        if (issuesForDetailsPanel.none { it.id == event.issue.id }) {
            LOGGER.debug("Adding ${event.issue} to show in the details pane")
            issuesForDetailsPanel.add(event.issue)
        }
    }

    @Subscribe
    fun remove(event: CloseIssueDetailsRequestEvent): Boolean {
        LOGGER.debug("Removing ${event.issue} from details pane")
        return issuesForDetailsPanel.removeIf { it.id == event.issue.id }
    }
}