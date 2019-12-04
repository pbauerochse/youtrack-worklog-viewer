package de.pbauerochse.worklogviewer.plugins.state

import java.time.LocalDate

/**
 * Contains state of the youtrack worklog viewer ui
 */
interface UIState {

    /**
     * The data context of the currently visible tab / project
     * in the main application window
     */
    val currentlyVisibleTab: TabContext?

    /**
     * The value currently selected in the start date picker
     */
    val currentStartValue: LocalDate?

    /**
     * The value currently selected in the end date picker
     */
    val currentEndValue: LocalDate?

}