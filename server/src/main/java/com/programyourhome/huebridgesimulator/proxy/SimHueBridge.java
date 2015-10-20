package com.programyourhome.huebridgesimulator.proxy;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.programyourhome.huebridgesimulator.model.lights.SimHueLight;
import com.programyourhome.huebridgesimulator.model.menu.Menu;
import com.programyourhome.huebridgesimulator.model.menu.MenuItem;

/**
 * This class is the actual simulator in the sense that is connects the hue requests that come in to the menu
 * that should provide the data and act on the 'light' switching. In this class the translation is made from menu items to simulated
 * hue lights and vice versa.
 */
@Component
public class SimHueBridge {

    @Inject
    private Menu menu;

    @Value("${simulator.prependIndex}")
    private boolean prependIndex;

    /**
     * Get the menu items that should be available and wrap their data in simulated light DTO's.
     *
     * @return a map containing all the light data
     */
    public Map<String, SimHueLight> getLights() {
        final Map<String, SimHueLight> lights = new HashMap<>();
        int index = 1;
        final MenuItem[] currentMenu = this.menu.getCurrentMenu();
        for (final MenuItem menuItem : currentMenu) {
            String lightName = menuItem.getName();
            if (this.prependIndex) {
                String indexString = "" + index;
                // If the total number of items is larger than or equal to 10, use 2 positions for the index
                // to ensure lexicographical ordering.
                if (currentMenu.length >= 10) {
                    indexString = String.format("%02d", index);
                }
                lightName = indexString + ". " + lightName;
            }
            lights.put("" + index, new SimHueLight(index, lightName, menuItem.getColor().toAwtColor(), menuItem.isOn()));
            index++;
        }
        return lights;
    }

    /**
     * Act on a light switch that was made for a particular simulated light.
     * This action is forwarded to the menu as a menu item selected event.
     *
     * @param index the index of the light
     * @param on whether the light should be turned on or off.
     */
    public void switchLight(final int index, final boolean on) {
        this.menu.menuItemClicked(this.menu.getCurrentMenu()[index - 1].getName(), on);
    }
}
