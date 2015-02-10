package com.programyourhome.huebridgesimulator.model.menu;

import java.awt.Color;

public class SimColor {

    private int red;
    private int green;
    private int blue;

    public SimColor() {
    }

    public SimColor(final Color color) {
        this(color.getRed(), color.getGreen(), color.getBlue());
    }

    public SimColor(final int red, final int green, final int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public int getRed() {
        return this.red;
    }

    public void setRed(final int red) {
        this.red = red;
    }

    public int getGreen() {
        return this.green;
    }

    public void setGreen(final int green) {
        this.green = green;
    }

    public int getBlue() {
        return this.blue;
    }

    public void setBlue(final int blue) {
        this.blue = blue;
    }

    public Color toAwtColor() {
        return new Color(this.red, this.green, this.blue);
    }

}
