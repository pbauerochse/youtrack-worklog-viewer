package de.pbauerochse.worklogviewer.youtrack;

/**
 * Interface that allows updating the progress
 * of a certain task
 */
public interface ProgressCallback {

    void incrementProgress(String message, int amount);

    default void incrementProgress(String message) {
        incrementProgress(message, 10);
    }

}
