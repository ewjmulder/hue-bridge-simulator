package com.programyourhome.huebridgesimulator.proxy;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.programyourhome.huebridgesimulator.model.lights.SimHueLight;
import com.programyourhome.huebridgesimulator.model.menu.Menu;
import com.programyourhome.huebridgesimulator.model.menu.MenuItem;

@Component
public class SimHueBridge {

    @Autowired
    private Menu menu;

    public Map<String, SimHueLight> getLights() {
        final Map<String, SimHueLight> lights = new HashMap<String, SimHueLight>();
        int index = 1;
        for (final MenuItem menuItem : this.menu.getCurrentMenu()) {
            lights.put("" + index, new SimHueLight(index, menuItem.getName(), menuItem.getColor()));
            index++;
        }
        return lights;
    }

    public void switchLight(final int index, final boolean on) {
        // Do we care if it is on or off?
        // TODO: refactoring around index and menu item selection (index in menu item DTO?)
        this.menu.menuItemSelected(this.menu.getCurrentMenu().get(index - 1));
    }
}
