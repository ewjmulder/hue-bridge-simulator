package com.programyourhome.huebridgesimulator.model;

import java.awt.Color;

/**
 * Very basic DTO for an RGB color, containing just three numbers for red, green and blue.
 * This implementation is useful because it's very easy to JSON (de)serialize.
 * You can go to and from a java.awt.Color object using the methods in this class.
 */
public class SimColor {

    public static final SimColor RED = new SimColor(255, 0, 0);
    public static final SimColor GREEN = new SimColor(0, 255, 0);
    public static final SimColor BLUE = new SimColor(0, 0, 255);

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
