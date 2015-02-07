package com.programyourhome.huebridgesimulator.model;

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
