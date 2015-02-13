package com.programyourhome.huebridgesimulator.model.menu;

/**
 * DTO for a menu item. It contains a name, a state (on/off) and a color.
 */
public class MenuItem {

    private String name;
    private boolean on;
    private SimColor color;

    public MenuItem() {
    }

    public MenuItem(final String name) {
        this(name, false);
    }

    public MenuItem(final String name, final boolean on) {
        this(name, on, new SimColor(0, 255, 0));
    }

    public MenuItem(final String name, final boolean on, final SimColor color) {
        this.name = name;
        this.on = on;
        this.color = color;
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
