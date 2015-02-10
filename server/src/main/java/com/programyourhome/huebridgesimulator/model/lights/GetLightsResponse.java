package com.programyourhome.huebridgesimulator.model.lights;

import java.util.HashMap;

import com.programyourhome.huebridgesimulator.model.connection.HueBridgeResponse;

public class GetLightsResponse extends HashMap<String, SimHueLight> implements HueBridgeResponse {

    private static final long serialVersionUID = 1L;

}
