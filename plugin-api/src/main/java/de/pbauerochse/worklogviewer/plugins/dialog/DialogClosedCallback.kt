package de.pbauerochse.worklogviewer.plugins.dialog

@FunctionalInterface
interface DialogClosedCallback {
    fun invoke()
}