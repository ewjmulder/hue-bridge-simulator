package com.programyourhome.huebridgesimulator.lightsources;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.programyourhome.huebridgesimulator.config.Config;
import com.programyourhome.huebridgesimulator.config.GetState;
import com.programyourhome.huebridgesimulator.config.responsefilter.IResponseFilter;
import com.programyourhome.huebridgesimulator.model.SimColor;
import com.programyourhome.huebridgesimulator.model.SimLight;
import com.programyourhome.huebridgesimulator.model.SimLightSource;

/**
 * A light source implementation that takes it's data from a configuration file.
 * The configuration file should be according to the schema of the config XSD.
 * It contains one or more simulated light source definitions, with each
 * a section on how to get the current state of the light source and
 * a section on how to change the state of the light source.
 * See the XSD and README for more information.
 */
@Component
@ConditionalOnProperty("backend.mode.config")
public class ConfigLightSource implements SimLightSource {

    private static final String XSD_LOCATION = "/config.xsd";

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Inject
    private ApplicationContext applicationContext;

    @Value("${backend.rest.config.filename}")
    private String configFilename;

    private Config config;

    @PostConstruct
    public void init() {
        File propertiesFile = new File(System.getProperty("simulator.properties.location"));
        File lightSourceConfigFile = new File(propertiesFile.getParentFile() + "/" + this.configFilename);
        this.config = this.loadConfig(lightSourceConfigFile);
    }

    /**
     * Load the config as XML from a file and validate the contents against the XSD.
     * Returns the deserialized Config object if all was successful or throws a IllegalStateException.
     *
     * @param lightSourceConfigFile file that contains the light source config xml
     * @return the deserialized Config object
     */
    private Config loadConfig(final File lightSourceConfigFile) {
        try {
            final JAXBContext context = JAXBContext.newInstance(Config.class);
            final Unmarshaller unmarshaller = context.createUnmarshaller();
            final SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            unmarshaller.setSchema(factory.newSchema(this.getClass().getResource(XSD_LOCATION)));
            return (Config) unmarshaller.unmarshal(lightSourceConfigFile);
        } catch (final Exception e) {
            throw new IllegalStateException("Exception occured during loading of config file: [" + lightSourceConfigFile + "].", e);
        }
    }

    @Override
    public SimLight[] getSimLights() {
        return this.config.getSimLights().stream()
                .map(this::createSimLight)
                .toArray(SimLight[]::new);
    }

    private com.programyourhome.huebridgesimulator.config.SimLight getSimLightById(final int id) {
        return this.config.getSimLights().stream()
                .filter(simLight -> simLight.getId() == id)
                .findFirst()
                .get();
    }

    private SimLight createSimLight(final com.programyourhome.huebridgesimulator.config.SimLight simLightInput) {
        SimLight simLightOutput = new SimLight(simLightInput.getId(), simLightInput.getName());
        if (simLightInput.getColor() != null) {
            simLightOutput.setColor(new SimColor(simLightInput.getColor().getRed(), simLightInput.getColor().getGreen(), simLightInput.getColor().getBlue()));
        }
        GetState getState = simLightInput.getGetState();
        try {
            String response = IOUtils.toString(new URL(getState.getUrl()).openStream(), Charset.forName("UTF-8"));
            // We can just call all of these one after the other, since there will only be one filled in.
            this.applicationContext.getBeansOfType(IResponseFilter.class).values().stream()
                    .forEach(responseFilterer -> responseFilterer.process(response, getState, simLightOutput));
        } catch (Exception e) {
            // Signal the error to the user as a postfix of the name
            simLightOutput.setName(simLightOutput.getName() + " - Error");
            this.log.error("Exception during getting state of sim light [" + simLightInput.getName() + "]", e);
        }
        return simLightOutput;
    }

    @Override
    public void setSimLightState(final int id, final boolean on) {
        this.log.info("Set sim light state for id " + id + " to " + on);
        com.programyourhome.huebridgesimulator.config.SimLight simLight = this.getSimLightById(id);
        String urlString;
        if (on) {
            urlString = simLight.getSetState().getUrlOn();
        } else {
            urlString = simLight.getSetState().getUrlOff();
        }
        try {
            this.log.debug("Requesting URL [" + urlString + "]");
            Object response = new URL(urlString).getContent();
            this.log.debug("Response is: [" + response + "]");
        } catch (Exception e) {
            this.log.error("Exception during setting state of sim light [" + simLight.getName() + "] to " + on, e);
        }
    }

}
