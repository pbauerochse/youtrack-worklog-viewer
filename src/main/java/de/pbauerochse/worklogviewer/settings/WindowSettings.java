package de.pbauerochse.worklogviewer.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Settings concerning the location and size
 * of the application window
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WindowSettings {

    private int width = 800;
    private int height = 600;
    private int positionX = 0;
    private int positionY = 0;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getPositionX() {
        return positionX;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

}
