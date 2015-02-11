package com.programyourhome.huebridgesimulator.proxy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.programyourhome.huebridgesimulator.model.connection.HueBridgeResponse;
import com.programyourhome.huebridgesimulator.model.lights.GetLightsResponse;
import com.programyourhome.huebridgesimulator.model.lights.SimHueLight;
import com.programyourhome.huebridgesimulator.model.menu.Menu;
import com.programyourhome.huebridgesimulator.model.menu.MenuItem;

@Component
public class SimHueBridge {

    @Autowired
    private Menu menu;

    public HueBridgeResponse getLights() {
        final GetLightsResponse lights = new GetLightsResponse();
        int index = 1;
        for (final MenuItem menuItem : this.menu.getCurrentMenu()) {
            lights.put("" + index, new SimHueLight(index, menuItem.getName(), menuItem.getColor().toAwtColor(), menuItem.isOn()));
            index++;
        }
        return lights;
    }

    public void switchLight(final int index, final boolean on) {
        this.menu.menuItemClicked(this.menu.getCurrentMenu()[index - 1].getName(), on);
    }
}
