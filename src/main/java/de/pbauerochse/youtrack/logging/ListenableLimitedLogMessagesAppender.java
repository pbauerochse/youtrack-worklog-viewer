package de.pbauerochse.youtrack.logging;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrick Bauerochse
 * @since 01.07.15
 */
public class ListenableLimitedLogMessagesAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private static ListenableLimitedLogMessagesAppender instance;

    private PatternLayout layout;

    private List<LogMessageListener> listenerList = new ArrayList<>();

    private LimitedLogMessageBuilder logMessageCache = new LimitedLogMessageBuilder(3000);

    public static ListenableLimitedLogMessagesAppender getInstance() {
        return instance;
    }

    @Override
    public void start() {
        super.start();
        instance = this;
        addListener(logMessageCache);
    }

    @Override
    public void stop() {
        super.stop();
        layout.stop();
        removeListener(logMessageCache);
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (!isStarted()) {
            return;
        }

        String formattedString = layout.doLayout(eventObject);
        listenerList.forEach(logMessageListener -> logMessageListener.onLogMessage(formattedString, eventObject));
    }

    public void addListener(LogMessageListener listener) {
        listenerList.add(listener);
    }

    public void removeListener(LogMessageListener listener) {
        listenerList.remove(listener);
    }

    public String getLogMessages() {
        return logMessageCache.getAllMessages();
    }

    public void setMaxLogLines(int maxLogLines) {
        logMessageCache.setMaxLogLines(maxLogLines);
    }

    public void setPattern(String pattern) {
        layout = new PatternLayout();
        layout.setPattern(pattern);
        layout.setContext(context);
        layout.setOutputPatternAsHeader(true);
        layout.start();
    }
}
