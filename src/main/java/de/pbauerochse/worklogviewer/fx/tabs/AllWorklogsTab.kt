package de.pbauerochse.worklogviewer.fx.tabs

import de.pbauerochse.worklogviewer.util.FormattingUtil.getFormatted
import de.pbauerochse.worklogviewer.youtrack.TimeReport
import org.slf4j.LoggerFactory

/**
 * Tab, that shows all worklogs contained in the
 * [TimeReport], no matter what author or project
 */
internal class AllWorklogsTab : WorklogsTab(getFormatted("view.main.tabs.all")) {

    fun update(report: TimeReport) {

    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(AllWorklogsTab::class.java)
    }
}
