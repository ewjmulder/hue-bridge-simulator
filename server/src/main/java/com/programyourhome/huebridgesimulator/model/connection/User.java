package com.programyourhome.huebridgesimulator.model.connection;

public class User {

    private final String username;
    private final String deviceType;

    public User(final String username, final String deviceType) {
        this.username = username;
        this.deviceType = deviceType;
    }

    public String getUsername() {
        return this.username;
    }

    public String getDeviceType() {
        return this.deviceType;
    }

}
