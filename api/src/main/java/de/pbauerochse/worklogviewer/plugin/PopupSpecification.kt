package de.pbauerochse.worklogviewer.plugin

data class PopupSpecification(
    val title: String,
    val modal: Boolean = false,
    val onClose: (() -> Unit)? = null
)