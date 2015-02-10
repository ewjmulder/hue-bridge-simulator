package com.programyourhome.huebridgesimulator.model.connection;

/**
 * JSON DTO for the POST request on /api, for connecting a new user to the bridge.
 * It contains a device type and optionally a username to use for the new user.
 */
public class ConnectionRequest {

    private String devicetype;

    private String username;

    public String getDevicetype() {
        return this.devicetype;
    }

    public void setDevicetype(final String devicetype) {
        this.devicetype = devicetype;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

}
