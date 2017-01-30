package com.programyourhome.huebridgesimulator.server;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import com.programyourhome.huebridgesimulator.ComponentScanBase;

/**
 * The main Spring Boot entry point. Contains annotations about component scan, configuration and property location.
 */
@ComponentScan(basePackageClasses = ComponentScanBase.class)
@EnableAutoConfiguration
@PropertySource("file:${simulator.properties.location}")
public class HueBridgeSimulatorServer {

    public static void startServer() {
        final String usageMessage = "Please provide the correct path to the simulator property location with: -Dsimulator.properties.location=/path/to/file";
        final String simulatorPropertyLocation = System.getProperty("simulator.properties.location");
        if (simulatorPropertyLocation == null) {
            System.out.println("No value provided for property 'simulator.properties.location'.");
            System.out.println(usageMessage);
            System.exit(-1);
        }
        final File propertiesFile = new File(simulatorPropertyLocation);
        if (!propertiesFile.exists()) {
            System.out.println("Property file not found: '" + simulatorPropertyLocation + "'.");
            System.out.println(usageMessage);
            System.exit(-1);
        }
        System.out.println("Using properties in file: " + propertiesFile.getAbsolutePath());

        // Set the Spring config location file to the Program Your Home properties file, so it will pick up all
        // Spring boot config from there as well. Note: this must be done like this instead of using a @PropertySource
        // annotation, because otherwise the logging properties will not be picked up. They must be available
        // very early in the boot process, see also: https://github.com/spring-projects/spring-boot/issues/2709
        System.setProperty("spring.config.location", propertiesFile.toURI().toString());
        final SpringApplication application = new SpringApplication(HueBridgeSimulatorServer.class);
        application.run(new String[0]);
    }

}