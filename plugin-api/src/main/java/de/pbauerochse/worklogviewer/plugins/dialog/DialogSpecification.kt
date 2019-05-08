package de.pbauerochse.worklogviewer.plugins.dialog

/**
 * Defines parameters needed to display a dialog
 * in the main application
 */
data class DialogSpecification(

    /**
     * The title of the popup dialog window
     */
    val title: String,

    /**
     * if set to `true` the dialog will not have a close button,
     * instead, view will need to take care of that
     */
    val modal: Boolean = false,

    /**
     * an optional callback, which will be invoked
     * when the dialog has been closed
     */
    val onClose: DialogClosedCallback? = null
)