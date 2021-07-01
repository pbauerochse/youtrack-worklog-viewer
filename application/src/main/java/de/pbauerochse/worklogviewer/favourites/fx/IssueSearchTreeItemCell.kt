package de.pbauerochse.worklogviewer.favourites.fx

import javafx.scene.control.Tooltip
import javafx.scene.control.TreeCell
import javafx.scene.control.TreeView
import javafx.util.Callback

/**
 * A [TreeCell] that displays [FavouriteItem]s
 */
class IssueSearchTreeItemCell : TreeCell<FavouriteItem>() {

    override fun updateItem(item: FavouriteItem?, empty: Boolean) {
        super.updateItem(item, empty)

        styleClass.removeAll(FavouritesStyleClasses.ALL)
        tooltip = item?.label?.let { Tooltip(it) }
        text = item?.label
        contextMenu = item?.contextMenu
        item?.styleClasses?.let { styleClass.addAll(it) }
    }

    companion object {
        fun cellFactory(): Callback<TreeView<FavouriteItem>, TreeCell<FavouriteItem>> = Callback { IssueSearchTreeItemCell() }
    }

}