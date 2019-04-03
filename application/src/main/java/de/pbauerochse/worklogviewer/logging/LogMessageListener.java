package de.pbauerochse.worklogviewer.logging;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Listener that gets notified in regular intervals
 * of the most recent log messages
 */
@FunctionalInterface
public interface LogMessageListener {

    void onLogMessage(@NotNull List<String> messages);

}
