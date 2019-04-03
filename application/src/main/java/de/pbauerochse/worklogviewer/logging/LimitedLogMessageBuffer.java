package de.pbauerochse.worklogviewer.logging;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

/**
 * "RingBuffer" to hold a limited amount of log messages
 */
class LimitedLogMessageBuffer implements LogMessageListener {

    private LinkedList<String> logMessages;
    private int maxLogLines;

    LimitedLogMessageBuffer(int maxLogLines) {
        this.maxLogLines = Math.max(maxLogLines, 1);
        this.logMessages = new LinkedList<>();
    }

    @Override
    public void onLogMessage(@NotNull List<String> messages) {
        logMessages.addAll(messages);
        while (logMessages.size() > maxLogLines) {
            logMessages.pop();
        }
    }

    void setMaxLogLines(int maxLogLines) {
        this.maxLogLines = maxLogLines;
    }

    List<String> getAllMessages() {
        return logMessages;
    }

}
