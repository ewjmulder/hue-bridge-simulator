package com.programyourhome.huebridgesimulator.lightsources;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.programyourhome.huebridgesimulator.model.SimColor;
import com.programyourhome.huebridgesimulator.model.SimLight;
import com.programyourhome.huebridgesimulator.model.SimLightSource;

/**
 * A simple test light source to see if the hue bridge simulator 'lights'
 * are correctly picked up by a screen based device that you want to 'fool'.
 */
@Component
@ConditionalOnProperty("backend.mode.test.screen")
public class ScreenTestLightSource implements SimLightSource {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final List<SimLight> simLights;

    public ScreenTestLightSource() {
        this.simLights = new ArrayList<>();
        this.simLights.add(new SimLight(1, "Test sim light"));
        this.simLights.add(new SimLight(2, "Test red", false, SimColor.RED));
        this.simLights.add(new SimLight(3, "Test green", false, SimColor.GREEN));
        this.simLights.add(new SimLight(4, "Test blue", false, SimColor.BLUE));
    }

    @Override
    public SimLight[] getSimLights() {
        return this.simLights.toArray(new SimLight[0]);
    }

    @Override
    public void setSimLightState(final int id, final boolean on) {
        this.log.info("Set sim light state for id " + id + " to " + on);
        this.getSimLighthById(id).setOn(on);
    }

}
