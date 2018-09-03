package de.pbauerochse.worklogviewer.connector

/**
 * Interface that allows updating the progress
 * of a certain task
 */
interface ProgressCallback {

    /**
     * Update the current progress
     * @param message the message to display
     * @param amount the amount of progress in percent
     */
    fun setProgress(message: String, amount: Double)

    fun setProgress(message: String, amount: Int) {
        setProgress(message, amount.toDouble())
    }

}
