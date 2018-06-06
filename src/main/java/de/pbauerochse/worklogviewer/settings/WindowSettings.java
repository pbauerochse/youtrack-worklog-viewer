package de.pbauerochse.worklogviewer.settings;

/**
 * Settings concerning the location and size
 * of the application window
 */
public interface WindowSettings {

    /**
     * The width of the main window
     */
    int getWidth();

    /**
     * The height of the main window
     */
    int getHeight();

    /**
     * The x location of the window in
     * screen pixels
     */
    int getPositionX();

    /**
     * The y location of the window in
     * screen pixels
     */
    int getPositionY();

}
