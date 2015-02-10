package com.programyourhome.huebridgesimulator.model.menu;

public class MenuItem {

    private String name;
    private SimColor color;
    private boolean on;

    public MenuItem() {
    }

    public MenuItem(final String name, final SimColor color, final boolean on) {
        this.name = name;
        this.color = color;
        this.on = on;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public SimColor getColor() {
        return this.color;
    }

    public void setColor(final SimColor color) {
        this.color = color;
    }

    public boolean isOn() {
        return this.on;
    }

    public void setOn(final boolean on) {
        this.on = on;
    }

}
