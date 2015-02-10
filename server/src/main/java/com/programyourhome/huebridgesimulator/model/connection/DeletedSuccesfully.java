package com.programyourhome.huebridgesimulator.model.connection;

import java.util.ArrayList;

import com.programyourhome.huebridgesimulator.model.connection.DeletedSuccesfully.Wrapper;

/**
 * JSON DTO for the api response of deleting a new user.
 */
public class DeletedSuccesfully extends ArrayList<Wrapper> implements HueBridgeResponse {

    private static final long serialVersionUID = 1L;

    public DeletedSuccesfully(final String username) {
        this.add(new Wrapper(username));
    }

    public class Wrapper {

        private String success;

        public Wrapper(final String username) {
            this.success = "/config/whitelist/" + username + " deleted.";
        }

        public String getSuccess() {
            return this.success;
        }

        public void setSuccess(final String success) {
            this.success = success;
        }

    }

}