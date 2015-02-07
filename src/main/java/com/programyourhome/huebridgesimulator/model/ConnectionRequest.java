package com.programyourhome.huebridgesimulator.model;

public class ConnectionRequest {

    private String devicetype;

    // Enable JSON parser for skipping unused props: http://java.dzone.com/articles/customizing

    // private String username;

    public String getDevicetype() {
        return this.devicetype;
    }

    public void setDevicetype(final String devicetype) {
        this.devicetype = devicetype;
    }

    // public String getUsername() {
    // return this.username;
    // }
    //
    // public void setUsername(final String username) {
    // this.username = username;
    // }

}
