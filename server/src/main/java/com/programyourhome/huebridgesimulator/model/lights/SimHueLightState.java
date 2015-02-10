package com.programyourhome.huebridgesimulator.model.lights;

import java.awt.Color;

import com.programyourhome.huebridgesimulator.model.lights.philips.PHUtilities;

public class SimHueLightState {

    private boolean on;
    private int bri;
    private int hue;
    private int sat;
    private float[] xy;
    private int ct;
    private String alert;
    private String effect;
    private String colormode;
    private boolean reachable;

    public SimHueLightState() {
        this(null, false);
    }

    public SimHueLightState(final Color color, final boolean on) {
        this.on = on;
        this.bri = 254;
        this.hue = 0;
        this.sat = 0;
        if (color != null) {
            this.xy = PHUtilities.calculateXYFromRGB(color.getRed(), color.getGreen(), color.getBlue(), "LCT001");
        } else {
            this.xy = new float[] { 0.0f, 0.0f };
        }
        this.ct = 500;
        this.colormode = "xy";
        this.reachable = true;
    }

    public boolean isOn() {
        return this.on;
    }

    public void setOn(final boolean on) {
        this.on = on;
    }

    public int getBri() {
        return this.bri;
    }

    public void setBri(final int bri) {
        this.bri = bri;
    }

    public int getHue() {
        return this.hue;
    }

    public void setHue(final int hue) {
        this.hue = hue;
    }

    public int getSat() {
        return this.sat;
    }

    public void setSat(final int sat) {
        this.sat = sat;
    }

    public float[] getXy() {
        return this.xy;
    }

    public void setXy(final float[] xy) {
        this.xy = xy;
    }

    public int getCt() {
        return this.ct;
    }

    public void setCt(final int ct) {
        this.ct = ct;
    }

    public String getAlert() {
        return this.alert;
    }

    public void setAlert(final String alert) {
        this.alert = alert;
    }

    public String getEffect() {
        return this.effect;
    }

    public void setEffect(final String effect) {
        this.effect = effect;
    }

    public String getColormode() {
        return this.colormode;
    }

    public void setColormode(final String colormode) {
        this.colormode = colormode;
    }

    public boolean isReachable() {
        return this.reachable;
    }

    public void setReachable(final boolean reachable) {
        this.reachable = reachable;
    }

}
