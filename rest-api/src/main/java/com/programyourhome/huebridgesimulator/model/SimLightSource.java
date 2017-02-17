package com.programyourhome.huebridgesimulator.model;

import java.util.Arrays;

/**
 * A simulated light 'source', meaning an implementing class can provide the data of simulated lights and
 * also act on a state change for a certain light.
 */
public interface SimLightSource {

    /**
     * Get the array of simulated lights.
     *
     * @return the array of simulated lights
     */
    public SimLight[] getSimLights();

    /**
     * Notify the light source that a simulated light with a certain id is supposed
     * to be put in state on (true) or off (false).
     *
     * @param id the id of the simulated light
     * @param on on (true) or off (false)
     */
    public void setSimLightState(int id, boolean on);

    /**
     * Default implementation of looking up a simulated light by id in the array of simulated lights.
     * This implementation assumes the searched light actually exists and will throw an exception otherwise.
     *
     * @param id the id of the simulated light
     * @return the simulated light
     */
    default SimLight getSimLighthById(final int id) {
        return Arrays.asList(getSimLights()).stream()
                .filter(simLight -> simLight.getId() == id)
                .findFirst()
                .get();
    }

}
