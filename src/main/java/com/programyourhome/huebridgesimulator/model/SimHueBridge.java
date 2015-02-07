package com.programyourhome.huebridgesimulator.model;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class SimHueBridge {

    public Map<String, SimHueLight> getLights() {
        final Map<String, SimHueLight> lights = new HashMap<String, SimHueLight>();
        lights.put("1", new SimHueLight("Dummy light"));
        lights.put("2", new SimHueLight("Some other light"));
        return lights;
    }

}
