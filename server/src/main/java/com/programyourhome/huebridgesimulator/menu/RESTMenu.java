package com.programyourhome.huebridgesimulator.menu;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.programyourhome.huebridgesimulator.model.menu.Menu;
import com.programyourhome.huebridgesimulator.model.menu.MenuItem;

@Component
@PropertySource("classpath:com/programyourhome/huebridgesimulator/config/properties/simulator.properties")
// TODO: ConditionalOnProperty backend.mode
public class RESTMenu implements Menu {

    @Value("${backend.host}")
    private String backendHost;

    @Value("${backend.port}")
    private int backendPort;

    @Value("${backend.basePath}")
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
    public void menuItemSelected(final MenuItem menuItem) {
        this.restTemplate.put(this.buildBackendUrlMenuItemSelected(menuItem.getName()), null);
    }

    private String buildBackendUrlCurrentMenu() {
        return this.buildBackendBaseUrl() + "currentMenu";
    }

    private String buildBackendUrlMenuItemSelected(final String name) {
        return this.buildBackendBaseUrl() + "menuItemSelected/" + name;
    }

    private String buildBackendBaseUrl() {
        return "http://" + this.backendHost + ":" + this.backendPort + "/" + this.backendBasePath + "/";
    }

}
