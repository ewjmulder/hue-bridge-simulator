package com.programyourhome.huebridgesimulator;

import org.springframework.beans.factory.annotation.Value;

/**
 * Base class for classes that use the host, port and mac properties.
 * These are needed to broadcast and supply the configured values for connecting to the hue bridge simulator.
 */
public abstract class AbstractSimulatorPropertiesBase {

    @Value("${simulator.host}")
    private String simulatorHost;

    @Value("${simulator.port}")
    private int simulatorPort;

    @Value("${simulator.mac}")
    private String simulatorMac;

    protected String getSimulatorHost() {
        return this.simulatorHost;
    }

    protected int getSimulatorPort() {
        return this.simulatorPort;
    }

    protected String getSimulatorMac() {
        return this.simulatorMac;
    }

}
