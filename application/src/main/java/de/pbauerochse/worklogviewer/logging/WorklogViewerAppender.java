package de.pbauerochse.worklogviewer.logging;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

/**
 * Logback Appender for the worklog viewer log messages
 */
public class WorklogViewerAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private PatternLayout layout;

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (!isStarted()) {
            return;
        }

        String formattedString = layout.doLayout(eventObject);
        WorklogViewerLogs.appendLogMessage(formattedString);
    }

    @SuppressWarnings("unused")
    // used by logback
    public void setMaxLogLines(int maxLogLines) {
        WorklogViewerLogs.setMaxLogLines(maxLogLines);
    }

    @SuppressWarnings("unused")
    // used by logback
    public void setPattern(String pattern) {
        layout = new PatternLayout();
        layout.setPattern(pattern);
        layout.setContext(context);
        layout.setOutputPatternAsHeader(true);
        layout.start();
    }
}
