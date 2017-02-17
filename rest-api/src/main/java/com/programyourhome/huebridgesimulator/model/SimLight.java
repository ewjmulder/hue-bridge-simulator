package com.programyourhome.huebridgesimulator.model;

/**
 * DTO for a simulated light. It contains an id, a name, a state (on/off) and a color.
 */
public class SimLight {

    private int id;
    private String name;
    private boolean on;
    private SimColor color;

    public SimLight() {
    }

    public SimLight(final int id, final String name) {
        this(id, name, false);
    }

    public SimLight(final int id, final String name, final boolean on) {
        this(id, name, on, SimColor.GREEN);
    }

    public SimLight(final int id, final String name, final boolean on, final SimColor color) {
        this.id = id;
        this.name = name;
        this.on = on;
        this.color = color;
    }

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean isOn() {
        return this.on;
    }

    public void setOn(final boolean on) {
        this.on = on;
    }

    public SimColor getColor() {
        return this.color;
    }

    public void setColor(final SimColor color) {
        this.color = color;
    }

}
