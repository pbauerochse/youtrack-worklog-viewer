package de.pbauerochse.worklogviewer.timereport.fx.table.columns.context

import de.pbauerochse.worklogviewer.details.events.ShowIssueDetailsRequestEvent
import de.pbauerochse.worklogviewer.events.EventBus
import de.pbauerochse.worklogviewer.favourites.FavouritesService
import de.pbauerochse.worklogviewer.favourites.fx.FavouritesModel
import de.pbauerochse.worklogviewer.openInBrowser
import de.pbauerochse.worklogviewer.timereport.Issue
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import de.pbauerochse.worklogviewer.workitem.add.fx.AddWorkItemDialog
import javafx.beans.binding.Bindings
import javafx.beans.binding.Bindings.`when`
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.MenuItem
import org.slf4j.LoggerFactory
import java.time.LocalDate

class OpenIssueInBrowserMenuItem(issue: Issue) : MenuItem(getFormatted("contextmenu.issue.openinyoutrack", issue.humanReadableId)) {
    init {
        onAction = EventHandler { issue.externalUrl.openInBrowser() }
    }
}

class OpenIssueInDetailsPaneMenuItem(issue: Issue) : MenuItem(getFormatted("contextmenu.issue.openindetails", issue.humanReadableId)) {
    init {
        onAction = EventHandler { EventBus.publish(ShowIssueDetailsRequestEvent(issue)) }
    }
}

class AddWorkItemToIssueMenuItem(private val issue: Issue, private val date: LocalDate? = null, private val sceneProvider: () -> Scene) :
    MenuItem(getFormatted("contextmenu.issue.addworkitem", issue.humanReadableId)) {
    init {
        onAction = EventHandler { showAddWorkItemToIssueDialog() }
    }

    internal fun showAddWorkItemToIssueDialog() {
        AddWorkItemDialog.show(sceneProvider.invoke(), date, issue)
    }
}

class AddWorkItemToOtherIssueMenuItem(date: LocalDate? = null, private val sceneProvider: () -> Scene) : MenuItem(getFormatted("contextmenu.issue.addanyworkitem")) {
    init {
        onAction = EventHandler { AddWorkItemDialog.show(sceneProvider.invoke(), date) }
    }
}

class ToggleFavouriteMenuItem(issue: Issue) : MenuItem() {
    init {
        val issueAlreadyMarkedAsFavourite = Bindings.createBooleanBinding({ FavouritesService.isFavourite(issue) }, FavouritesModel.favouriteIssues)
        textProperty().bind(
            `when`(issueAlreadyMarkedAsFavourite)
                .then(getFormatted("contextmenu.issue.removefavourite", issue.humanReadableId))
                .otherwise(
                    getFormatted("contextmenu.issue.addfavourite", issue.humanReadableId)
                )
        )
        onAction = EventHandler {
            if (issueAlreadyMarkedAsFavourite.get()) {
                LoggerFactory.getLogger(ToggleFavouriteMenuItem::class.java).info("Removing ${issue.humanReadableId} from Favourites")
                FavouritesService.removeFavourite(issue)
            } else {
                LoggerFactory.getLogger(ToggleFavouriteMenuItem::class.java).info("Marking ${issue.humanReadableId} as Favourite")
                FavouritesService.addFavourite(issue)
            }
        }
    }
}
