package com.programyourhome.huebridgesimulator.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import com.programyourhome.huebridgesimulator.ComponentScanBase;

@ComponentScan(basePackageClasses = ComponentScanBase.class)
@EnableAutoConfiguration
public class HueBridgeSimulatorServer {

    public static void startServer() {
        SpringApplication.run(HueBridgeSimulatorServer.class, new String[0]);
    }

}