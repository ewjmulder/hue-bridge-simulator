package com.programyourhome.huebridgesimulator.model.connection;

/**
 * Simple DTO for keeping track of data about users.
 */
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

    @Override
    public String toString() {
        return "User [username = '" + this.username + "', deviceType = '" + this.deviceType + "']";
    }

}
