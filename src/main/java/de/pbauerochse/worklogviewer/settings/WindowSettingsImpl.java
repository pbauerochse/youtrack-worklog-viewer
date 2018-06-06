package de.pbauerochse.worklogviewer.settings;

class WindowSettingsImpl implements WindowSettings {

    private int width = 800;
    private int height = 600;
    private int positionX = 0;
    private int positionY = 0;

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getPositionX() {
        return positionX;
    }

    @Override
    public int getPositionY() {
        return positionY;
    }
}
