package com.programyourhome.huebridgesimulator.menu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.programyourhome.huebridgesimulator.model.menu.Menu;
import com.programyourhome.huebridgesimulator.model.menu.MenuItem;

/**
 * A Menu implementation that forwards the menu method calls to another REST server.
 * Mainly meant to connect the hue bridge simulator to the program your home server.
 */
@Component
@ConditionalOnProperty("backend.mode.rest")
public class RESTMenu implements Menu {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${backend.rest.host}")
    private String backendHost;

    @Value("${backend.rest.port}")
    private int backendPort;

    @Value("${backend.rest.basePath}")
    private String backendBasePath;

    private final RestTemplate restTemplate;

    public RESTMenu() {
        this.restTemplate = new RestTemplate();
    }

    @Override
    public MenuItem[] getCurrentMenu() {
        return this.restTemplate.getForObject(this.buildBackendUrlCurrentMenu(), MenuItem[].class);
    }

    @Override
    public void menuItemClicked(final String menuItemName, final boolean on) {
        this.log.info("Menu item 'clicked': " + menuItemName);
        this.restTemplate.put(this.buildBackendUrlMenuItemSelected(menuItemName, on), null);
    }

    private String buildBackendUrlCurrentMenu() {
        return this.buildBackendBaseUrl() + "currentMenu";
    }

    private String buildBackendUrlMenuItemSelected(final String name, final boolean on) {
        return this.buildBackendBaseUrl() + "menuItemClicked/" + name + "/" + on;
    }

    private String buildBackendBaseUrl() {
        return "http://" + this.backendHost + ":" + this.backendPort + "/" + this.backendBasePath + "/";
    }

}
