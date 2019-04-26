package de.pbauerochse.worklogviewer.logging

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class LimitedLogMessageBufferTest {

    @Test
    fun test() {
        val messageBuilder = LimitedLogMessageBuffer(10)
        for (i in 0..19) {
            messageBuilder.onLogMessage(listOf(i.toString()))
        }

        assertEquals(listOf("10", "11", "12", "13", "14", "15", "16", "17", "18", "19"), messageBuilder.allMessages, "Expected the log messages to be limited")
    }

}
