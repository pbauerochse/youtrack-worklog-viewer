package de.pbauerochse.worklogviewer.fx.components

import de.pbauerochse.worklogviewer.connector.GroupByParameter
import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted

/**
 * Special GroupByParameter that indicates
 * that no grouping should be applied when
 * fetching the Worklogs
 */
class NoSelectionGroupByParameter : GroupByParameter {

    override val id: String = "_NO_SELECTION_"
    override fun getLabel(): String = getFormatted("view.main.groupby.nogroupby")

}