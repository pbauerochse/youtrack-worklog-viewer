package de.pbauerochse.youtrack.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.LinkedList;

/**
 * @author Patrick Bauerochse
 * @since 02.07.15
 */
public class LimitedLogMessageBuilder implements LogMessageListener {

    private static final int INITIAL_SIZE = 5000;

    private LinkedList<String> logMessages;

    private StringBuilder logMessageCache;

    private int maxLogLines;

    public LimitedLogMessageBuilder() {
        this(INITIAL_SIZE);
    }

    public LimitedLogMessageBuilder(int maxLogLines) {
        this.maxLogLines = Math.max(maxLogLines, 1);
        this.logMessages = new LinkedList<>();
        this.logMessageCache = new StringBuilder(this.maxLogLines * 150);
    }

    @Override
    public void onLogMessage(String formattedLogMessage, ILoggingEvent originalEvent) {

        logMessages.add(formattedLogMessage);
        logMessageCache.append(formattedLogMessage);

        while (logMessages.size() > maxLogLines) {
            String popped = logMessages.pop();
            int index = logMessageCache.indexOf(popped);

            logMessageCache.delete(index, index + popped.length());
        }
    }

    public String getAllMessages() {
        return logMessageCache.toString();
    }

    public int getMaxLogLines() {
        return maxLogLines;
    }

    public void setMaxLogLines(int maxLogLines) {
        this.maxLogLines = maxLogLines;
    }

    private class LogMessageIndizes {

        private int start;
        private int end;

        public LogMessageIndizes(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public int getStart() {
            return start;
        }

        public int getEnd() {
            return end;
        }
    }

}
