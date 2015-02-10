package com.programyourhome.huebridgesimulator.model.connection;

import java.util.ArrayList;

import com.programyourhome.huebridgesimulator.model.connection.ConnectedSuccesfully.Wrapper;

/**
 * JSON DTO for the api response of connecting a new user.
 */
public class ConnectedSuccesfully extends ArrayList<Wrapper> implements HueBridgeResponse {

    private static final long serialVersionUID = 1L;

    public ConnectedSuccesfully(final String username) {
        this.add(new Wrapper(username));
    }

    public class Wrapper {

        private Username success;

        public Wrapper(final String username) {
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

}
