package de.pbauerochse.youtrack.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * @author Patrick Bauerochse
 * @since 01.07.15
 */
@FunctionalInterface
public interface LogMessageListener {

    void onLogMessage(String formattedLogMessage, ILoggingEvent originalEvent);

}
