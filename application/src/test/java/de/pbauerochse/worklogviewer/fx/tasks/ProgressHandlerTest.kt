package de.pbauerochse.worklogviewer.fx.tasks

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ProgressHandlerTest {

    @Test
    fun `Progress starts at 0`() {
        val progress = ProgressHandler()
        assertEquals(0.0, progress.currentValue)
    }

    @Test
    fun `Setting a value higher than 100 defaults to 100`() {
        val progress = ProgressHandler()
        progress.setProgress("Some message", 150)

        assertEquals(100.0, progress.currentValue)
    }

    @Test
    fun `Setting a value below 0 defaults to 0`() {
        val progress = ProgressHandler()
        progress.setProgress("Some message", -150)

        assertEquals(0.0, progress.currentValue)
    }

    @Test
    fun `The upperLimit defines the maximum value at 100 percent`() {
        val progress = ProgressHandler(20.0, null)

        progress.setProgress("Some message", 50)
        assertEquals(10.0, progress.currentValue)

        progress.setProgress("Some message", 100)
        assertEquals(20.0, progress.currentValue)
    }

    @Test
    fun `Setting a progress higher than 100 percent defaults to the upperLimit`() {
        val progress = ProgressHandler(50.0, null)

        progress.setProgress("Some message", 500)
        assertEquals(50.0, progress.currentValue)
    }

    @Test
    fun `Starting a subprocess increments the parent progress`() {
        val mainProgress = ProgressHandler()
        val subProgress = mainProgress.subProgress(50) as ProgressHandler

        subProgress.setProgress("Subprogress message", 50)
        assertEquals(25.0, subProgress.currentValue)
        assertEquals(25.0, mainProgress.currentValue)
    }

    @Test
    fun `Test with multiple subprocesses`() {
        val mainProgress = ProgressHandler()
        val subProgressA = mainProgress.subProgress(50)
        val subProgressB = mainProgress.subProgress(50)

        subProgressA.setProgress("SubprogressA message", 50)
        subProgressB.setProgress("SubprogressB message", 10)

        assertEquals(30.0, mainProgress.currentValue)
    }

    @Test
    fun `A subprocess can not take more than the remaining progress of the main progress`() {
        val mainProgress = ProgressHandler()
        mainProgress.setProgress("Main Progress message", 25)

        val subProgressA = mainProgress.subProgress(100)
        assertEquals(75.0, (subProgressA as ProgressHandler).upperLimit)
    }

    @Test
    fun `Child progress messages are delegated to the parent progress`() {
        val mainProgress = ProgressHandler()

        mainProgress.setProgress("Main Progress message", 20)
        assertEquals("Main Progress message", mainProgress.currentMessage)

        val subProgress = mainProgress.subProgress(50)
        subProgress.setProgress("Subprogress message", 20)

        assertEquals("Subprogress message", mainProgress.currentMessage)
        assertEquals("Subprogress message", (subProgress as ProgressHandler).currentMessage)
    }

    @Test
    fun `increment progress increments by the given amount`() {
        val progress = ProgressHandler()
        progress.incrementProgress(10)

        assertEquals(10.0, progress.currentValue)
    }

    @Test
    fun `increment progress on a subprogress increments by the given amount`() {
        val mainProgress = ProgressHandler()
        mainProgress.setProgress("Message", 50)

        val subprogressA = mainProgress.subProgress(25)
        val subprogressB = mainProgress.subProgress(25)

        subprogressA.incrementProgress(50) // 50% of 25 = 12.5
        subprogressB.incrementProgress(75) // 75% of 25 = 18.75

        assertEquals(81.25, mainProgress.currentValue)
    }

    @Test
    fun `subprogress from a subprogress`() {
        // main progress at 50% of 100 = 50
        val mainProgress = ProgressHandler()
        mainProgress.setProgress("Message", 50)

        // subprogress which will consume the rest
        // of the available 50% from the main progress
        // subprogress internal percentage at 0%
        val subprogress = mainProgress.subProgress(50) as ProgressHandler

        // subprogress internal progress at 50% -> mainProgress at 75% = 75
        assertEquals(50.0, subprogress.upperLimit)
        assertEquals(0.0, subprogress.currentValue, "Subprogress should be at 0% internal value")
        assertEquals(50.0, mainProgress.currentValue, "Main Progress should be at 50% value")

        // adding 50% to subprogress
        subprogress.setProgress("Subprogress changed to 50%", 50)
        assertEquals(25.0, subprogress.currentValue, "Subprogress should have an absolute value of 25")
        assertEquals(75.0, mainProgress.currentValue, "Main Progress should be at 75% value")

        // sub sub progresses
        val subSubprogressA = subprogress.subProgress(50) as ProgressHandler // 50% of 50% of 50
        assertEquals(25.0, subSubprogressA.upperLimit)

        val subSubprogressB = subprogress.subProgress(50) as ProgressHandler
        assertEquals(25.0, subSubprogressB.upperLimit)

        subSubprogressA.setProgress("Sub Sub A", 50)
        assertEquals(12.5, subSubprogressA.currentValue)
        assertEquals(37.5, subprogress.currentValue)
        assertEquals(87.5, mainProgress.currentValue)
    }

}