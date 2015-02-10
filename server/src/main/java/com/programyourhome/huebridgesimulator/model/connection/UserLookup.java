package com.programyourhome.huebridgesimulator.model.connection;


public class UserLookup {

    private final User user;
    private final HueBridgeResponse error;

    public UserLookup(final User user, final HueBridgeResponse error) {
        this.user = user;
        this.error = error;
    }

    public User getUser() {
        return this.user;
    }

    public HueBridgeResponse getError() {
        return this.error;
    }

    public boolean hasError() {
        return this.error != null;
    }

}
