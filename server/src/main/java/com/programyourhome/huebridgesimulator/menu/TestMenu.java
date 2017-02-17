package com.programyourhome.huebridgesimulator.menu;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.programyourhome.huebridgesimulator.model.menu.Menu;
import com.programyourhome.huebridgesimulator.model.menu.MenuItem;
import com.programyourhome.huebridgesimulator.model.menu.SimColor;

/**
 * A simple test menu to see if the hue bridge simulator 'lights' are correctly picked up by the device that you want to 'fool'.
 */
@Component
@ConditionalOnProperty("backend.mode.test")
public class TestMenu implements Menu {

    private final List<MenuItem> menuItems;

    public TestMenu() {
        this.menuItems = new ArrayList<>();
        this.menuItems.add(new MenuItem("Item 1"));
        this.menuItems.add(new MenuItem("Item 2"));
        this.menuItems.add(new MenuItem("Item 3"));
        this.menuItems.add(new MenuItem("Reverse", false, new SimColor(Color.RED)));
    }

    @Override
    public MenuItem[] getCurrentMenu() {
        return this.menuItems.toArray(new MenuItem[0]);
    }

    @Override
    public void menuItemClicked(final String menuItemName, final boolean on) {
        if (menuItemName.startsWith("Item")) {
            this.getMenuItemByName(menuItemName).setOn(on);
        } else {
            Collections.swap(this.menuItems, 0, 3);
            Collections.swap(this.menuItems, 1, 2);
        }
    }

}
