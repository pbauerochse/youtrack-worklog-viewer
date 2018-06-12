package de.pbauerochse.worklogviewer.fx.components.treetable

import de.pbauerochse.worklogviewer.fx.tablecolumns.CellStyleClasses
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.Tooltip
import javafx.scene.control.TreeTableCell
import javafx.scene.control.TreeTableColumn
import javafx.util.Callback
import org.slf4j.LoggerFactory

/**
 * Displays the description and the id of the
 * Issue as a link to the YouTrack issue
 */
internal class IssueLinkColumn : TreeTableColumn<TreeTableRowModel, TreeTableRowModel>(getFormatted("view.main.issue")) {

    init {
        isSortable = false
        cellValueFactory = Callback { col -> SimpleObjectProperty(col.value.value) }
        cellFactory = Callback { _ -> IssueLinkCell() }


        //        setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().getValue()));
        //        setCellFactory(param -> {
        //            TreeTableCell<DisplayRow, DisplayRow> tableCell = new TreeTableCell<DisplayRow, DisplayRow>() {
        //
        //                @Override
        //                protected void updateItem(DisplayRow item, boolean empty) {
        //
        //                    getStyleClass().removeAll(ALL);
        //
        //                    if (empty) {
        //                        setText(StringUtils.EMPTY);
        //                        setTooltip(null);
        //                    } else {
        //                        if (item.isGrandTotalSummary()) {
        //                            setText(getFormatted("view.main.summary"));
        //                            setTooltip(null);
        //                            getStyleClass().add(SUMMARY_COLUMN_OR_CELL_CSS_CLASS);
        //                            setAlignment(Pos.CENTER_RIGHT);
        //                        } else if (item.isGroupContainer()) {
        //                            setText(item.getLabel());
        //                            setTooltip(new Tooltip(item.getLabel()));
        //                            getStyleClass().add(GROUP_COLUMN_OR_CELL_CSS_CLASS);
        //                            setAlignment(Pos.CENTER_LEFT);
        //                        } else {
        //                            setText(item.getIssueId().get() + " - " + item.getLabel());
        //                            setTooltip(new Tooltip(getText()));
        //                            getStyleClass().add(ISSUE_CELL_CSS_CLASS);
        //                            setAlignment(Pos.CENTER_LEFT);
        //                        }
        //                    }
        //                }
        //            };
        //
        //            tableCell.setOnMouseClicked(event -> {
        //                TreeTableCell<DisplayRow, DisplayRow> cell = (TreeTableCell<DisplayRow, DisplayRow>) event.getSource();
        //
        //                DisplayRow clickedWorklogItem = cell.getTreeTableRow().getItem();
        //                if (clickedWorklogItem != null && !clickedWorklogItem.isGrandTotalSummary() && !clickedWorklogItem.isGroupContainer()) {
        //                    LOGGER.debug("Selected item {}", clickedWorklogItem.getLabel());
        //                    Settings settings = SettingsUtil.getSettings();
        //                    String issueUrl = String.format("%s/issue/%s#tab=Time%%20Tracking", StringUtils.stripEnd(settings.getYouTrackConnectionSettings().getUrl(), "/"), clickedWorklogItem.getIssueId().get());
        //                    Platform.runLater(() -> WorklogViewer.getInstance().getHostServices().showDocument(issueUrl));
        //                }
        //            });
        //
        //            return tableCell;
        //        });
    }

    companion object {

        private val LOGGER = LoggerFactory.getLogger(IssueLinkColumn::class.java)
    }
}

class IssueLinkCell : TreeTableCell<TreeTableRowModel, TreeTableRowModel>() {

    init {
        onMouseClicked = EventHandler { _ -> onClick() }
    }

    override fun updateItem(item: TreeTableRowModel?, empty: Boolean) {
        super.updateItem(item, empty)
        LOGGER.debug("Updating Item $item")

        // TODO check if isIssue, isGroupBy, isSummary

        text = item?.issue?.fullTitle
        tooltip = Tooltip(item?.issue?.fullTitle)
        alignment = Pos.CENTER_LEFT

        styleClass.removeAll()
        styleClass.add(CellStyleClasses.ISSUE_CELL_CSS_CLASS)
    }

    private fun onClick() {
        LOGGER.debug("Clicked cell ")
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(IssueLinkCell::class.java)
    }

}
