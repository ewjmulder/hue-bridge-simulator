package com.programyourhome.huebridgesimulator.lightsources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.programyourhome.huebridgesimulator.model.SimLight;
import com.programyourhome.huebridgesimulator.model.SimLightSource;

/**
 * A light source implementation that forwards the calls to a REST service.
 * Can be used to connect the hue bridge simulator to a custom smart home server,
 * like the program your home server. Can also connect to a custom made proxy
 * that translates it to and from a 3rd party product or service.
 */
@Component
@ConditionalOnProperty("backend.mode.rest")
public class RESTLightSource implements SimLightSource {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${backend.rest.host}")
    private String backendHost;

    @Value("${backend.rest.port}")
    private int backendPort;

    @Value("${backend.rest.basePath}")
    private String backendBasePath;

    private final RestTemplate restTemplate;

    public RESTLightSource() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public SimLight[] getSimLights() {
        return this.restTemplate.getForObject(this.buildBackendUrlGetSimLights(), SimLight[].class);
    }

    @Override
    public void setSimLightState(final int id, final boolean on) {
        this.log.info("Set sim light state for id " + id + " to " + on);
        this.restTemplate.put(this.buildBackendUrlSetSimLightState(id, on), null);
    }

    private String buildBackendUrlGetSimLights() {
        return this.buildBackendBaseUrl() + "simLights";
    }

    private String buildBackendUrlSetSimLightState(final int id, final boolean on) {
        return this.buildBackendBaseUrl() + "simLights/" + id + "/" + on;
    }

    private String buildBackendBaseUrl() {
        return "http://" + this.backendHost + ":" + this.backendPort + "/" + this.backendBasePath + "/";
    }

}
