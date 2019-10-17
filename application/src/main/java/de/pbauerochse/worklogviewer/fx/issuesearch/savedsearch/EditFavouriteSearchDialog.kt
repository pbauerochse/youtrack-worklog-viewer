package de.pbauerochse.worklogviewer.fx.issuesearch.savedsearch

import de.pbauerochse.worklogviewer.settings.favourites.FavouriteSearch
import de.pbauerochse.worklogviewer.util.FormattingUtil
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.stage.Window

class EditFavouriteSearchDialog(private val search: FavouriteSearch, owner: Window?) : Dialog<FavouriteSearch>() {

    private val nameProperty: StringProperty = SimpleStringProperty(search.name)
    private val queryProperty: StringProperty = SimpleStringProperty(search.query)

    init {
        val loader = FXMLLoader(EditFavouriteSearchDialog::class.java.getResource("/fx/views/favourite-search.fxml"), FormattingUtil.RESOURCE_BUNDLE)
        val dialogContent = loader.load<Parent>()
        val controller = loader.getController<EditFavouriteSearchController>()
        controller.searchName.textProperty().bindBidirectional(nameProperty)
        controller.searchQuery.textProperty().bindBidirectional(queryProperty)

        // TODO styles and positioning
        // TODO main app backdrop

        initOwner(owner)
        title = getFormatted("dialog.issuesearch.groups.favourites.searches.edit.dialog.title")
        dialogPane.apply {
            buttonTypes.setAll(listOf(ButtonType.OK, ButtonType.CANCEL))
            content = dialogContent
        }

        val okButton = dialogPane.lookupButton(ButtonType.OK) as Button
        okButton.text = getFormatted("view.settings.save")
        okButton.disableProperty().bind(nameProperty.isEmpty.or(queryProperty.isEmpty))

        setResultConverter {
            return@setResultConverter when (it.buttonData) {
                ButtonBar.ButtonData.OK_DONE -> applyValues()
                else -> null
            }
        }
    }

    private fun applyValues(): FavouriteSearch {
        return search.apply {
            name = nameProperty.value
            query = queryProperty.value
        }
    }

}
