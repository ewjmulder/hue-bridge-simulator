package com.programyourhome.huebridgesimulator.model.lights;

import java.util.HashMap;
import java.util.Map;

import com.programyourhome.huebridgesimulator.model.connection.HueBridgeResponse;

/**
 * JSON DTO that extends a HashMap with the light name as key and the light data as value.
 * Functions as a marker class, so it can implement the HueBridgeResponse marker interface.
 */
public class GetLightsResponse extends HashMap<String, SimHueLight> implements HueBridgeResponse {

    private static final long serialVersionUID = 1L;

    public GetLightsResponse(final Map<String, SimHueLight> lights) {
        this.putAll(lights);
    }

}
