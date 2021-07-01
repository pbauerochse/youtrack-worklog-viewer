package de.pbauerochse.worklogviewer.favourites.fx

import javafx.scene.control.ContextMenu

/**
 * Data abstraction so that [de.pbauerochse.worklogviewer.favourites.issue.FavouriteIssue]s
 * and [de.pbauerochse.worklogviewer.favourites.searches.FavouriteSearch]es can be
 * shown in the same [javafx.scene.control.TableView]
 */
data class FavouriteItem(
    val label: String,
    val onSelect: (() -> Unit)? = null,
    val contextMenu : ContextMenu? = null,
    val styleClasses : Collection<String> = emptySet()
) {

    override fun toString(): String = label

    companion object {
        fun labeledCategoryHeaderItem(label: String): FavouriteItem {
            return FavouriteItem(
                label = label,
                styleClasses = listOf(FavouritesStyleClasses.CATEGORY_HEADER_CELL)
            )
        }
    }
}