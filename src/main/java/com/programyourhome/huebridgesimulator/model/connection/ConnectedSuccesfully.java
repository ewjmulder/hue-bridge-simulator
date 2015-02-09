package com.programyourhome.huebridgesimulator.model.connection;

/**
 * JSON DTO for the api response of connecting a new user.
 */
public class ConnectedSuccesfully {

    private Username success;

    public ConnectedSuccesfully(final String username) {
        this.success = new Username();
        this.success.username = username;
    }

    public Username getSuccess() {
        return this.success;
    }

    public void setSuccess(final Username success) {
        this.success = success;
    }

    public class Username {
        private String username;

        public String getUsername() {
            return this.username;
        }
    }

}
