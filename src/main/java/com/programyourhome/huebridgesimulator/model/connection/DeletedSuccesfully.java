package com.programyourhome.huebridgesimulator.model.connection;

/**
 * JSON DTO for the api response of deleting a new user.
 */
public class DeletedSuccesfully {

    private String success;

    public DeletedSuccesfully(final String username) {
        this.success = "/config/whitelist/" + username + " deleted.";
    }

    public String getSuccess() {
        return this.success;
    }

    public void setSuccess(final String success) {
        this.success = success;
    }

}