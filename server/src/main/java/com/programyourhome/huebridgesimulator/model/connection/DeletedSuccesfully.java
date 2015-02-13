package com.programyourhome.huebridgesimulator.model.connection;

import java.util.ArrayList;

import com.programyourhome.huebridgesimulator.model.connection.DeletedSuccesfully.Wrapper;

/**
 * JSON DTO for the api response of deleting a user.
 * The ArrayList<Wrapper> construct is needed to create the right type of JSON output, namely an array with one item.
 */
public class DeletedSuccesfully extends ArrayList<Wrapper> implements HueBridgeResponse {

    private static final long serialVersionUID = 1L;

    public DeletedSuccesfully(final String username) {
        this.add(new Wrapper(username));
    }

    public class Wrapper {

        private final String success;

        public Wrapper(final String username) {
            this.success = "/config/whitelist/" + username + " deleted.";
        }

        public String getSuccess() {
            return this.success;
        }

    }

}