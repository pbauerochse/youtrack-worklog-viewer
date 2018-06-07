package de.pbauerochse.worklogviewer.youtrack;

/**
 * Interface that allows updating the progress
 * of a certain task
 */
public interface ProgressCallback {

    void setProgress(String message, int percentageDone);

}
