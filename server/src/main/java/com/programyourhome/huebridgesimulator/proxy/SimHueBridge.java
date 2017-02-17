package com.programyourhome.huebridgesimulator.proxy;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.programyourhome.huebridgesimulator.model.SimLight;
import com.programyourhome.huebridgesimulator.model.SimLightSource;
import com.programyourhome.huebridgesimulator.model.lights.SimHueLight;

/**
 * This class is the actual simulator in the sense that is connects the hue requests that come in to
 * the simulated light source that should provide the data and act on the 'light' switching.
 * In this class the translation is made from simulated lights to simulated hue lights and vice versa.
 */
@Component
public class SimHueBridge {

    @Inject
    private SimLightSource simLightSource;

    @Value("${simulator.prependIndex}")
    private boolean prependIndex;

    /**
     * Get the simulated lights that should be available and wrap their data in simulated hue light DTO's.
     *
     * @return a map containing all the light data
     */
    public Map<String, SimHueLight> getLights() {
        final Map<String, SimHueLight> lights = new HashMap<>();
        // The index will be the (1-based) hue light id.
        int index = 1;
        final SimLight[] simLights = this.simLightSource.getSimLights();
        for (final SimLight simLight : simLights) {
            String lightName = simLight.getName();
            if (this.prependIndex) {
                String indexString = "" + index;
                // If the total number of items is larger than or equal to 10, use 2 positions for the index
                // to ensure lexicographical ordering.
                if (simLights.length >= 10) {
                    indexString = String.format("%02d", index);
                }
                lightName = indexString + ". " + lightName;
            }
            lights.put("" + index, new SimHueLight(index, lightName, simLight.getColor().toAwtColor(), simLight.isOn()));
            index++;
        }
        return lights;
    }

    /**
     * Act on a light switch that was made for a particular simulated hue light.
     * This action is forwarded to the simulated light source.
     *
     * @param index the index of the light
     * @param on whether the light should be turned on or off.
     */
    public void switchLight(final int index, final boolean on) {
        this.simLightSource.setSimLightState(this.simLightSource.getSimLights()[index - 1].getId(), on);
    }
}
