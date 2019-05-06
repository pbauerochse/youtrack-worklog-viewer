package de.pbauerochse.worklogviewer.tasks

/**
 * Interface that allows updating the progress
 * of a certain task
 */
interface Progress {

    /**
     * Update the current progress
     * @param message the message to display
     * @param progressInPercent the progressInPercent of progress in percent
     */
    fun setProgress(message: String, progressInPercent: Double)
    fun setProgress(message: String, progressInPercent: Int) = setProgress(message, progressInPercent.toDouble())

    fun incrementProgress(increment: Double)
    fun incrementProgress(increment : Int) = incrementProgress(increment.toDouble())

    fun incrementProgress(message: String, increment: Double)
    fun incrementProgress(message: String, increment : Int) = incrementProgress(message, increment.toDouble())

    /**
     * Starts a subprogress which also updates this progress.
     *
     * When you start a subprogress with a value of 50, it will consume
     * a maximum amount of 50 percent of the parent Progress, when it's
     * value reaches 100%
     *
     * Main Progress
     * \_ Childprogress(percentageOfParentProgress = 50) @ 100%
     * \_ Childprogress(percentageOfParentProgress = 20) @ 50%
     * \_ Childprogress(percentageOfParentProgress = 20) @ 100%
     * \_ Childprogress(percentageOfParentProgress = 10) @ 50%
     *
     * Results in a percentage for the main progress of (100% of 50 + 50% of 20 + 100% of 20 + 50% of 10 = 50 + 10 + 20 + 5 = 85% of the Main Progress)
     *
     * @param percentageOfParentProgress the upper limit of progress the subprocess may take up
     */
    fun subProgress(percentageOfParentProgress : Int) : Progress

}
