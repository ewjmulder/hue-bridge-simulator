package com.programyourhome.huebridgesimulator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:com/programyourhome/huebridgesimulator/config/properties/simulator.properties")
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
