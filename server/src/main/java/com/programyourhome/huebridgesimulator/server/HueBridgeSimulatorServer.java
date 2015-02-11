package com.programyourhome.huebridgesimulator.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import com.programyourhome.huebridgesimulator.ComponentScanBase;

@ComponentScan(basePackageClasses = ComponentScanBase.class)
@EnableAutoConfiguration
@PropertySource("classpath:com/programyourhome/huebridgesimulator/config/properties/simulator.properties")
public class HueBridgeSimulatorServer {

    public static void startServer() {
        SpringApplication.run(HueBridgeSimulatorServer.class, new String[0]);
    }

}