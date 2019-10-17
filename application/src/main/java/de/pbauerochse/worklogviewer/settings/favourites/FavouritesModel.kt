package de.pbauerochse.worklogviewer.settings.favourites

import javafx.collections.FXCollections
import javafx.collections.ObservableList

class FavouritesModel(private val favourites: Favourites) {
    val issues : ObservableList<FavouriteIssue> = FXCollections.observableList(favourites.issues)
    val searches : ObservableList<FavouriteSearch> = FXCollections.observableList(favourites.searches)
}
