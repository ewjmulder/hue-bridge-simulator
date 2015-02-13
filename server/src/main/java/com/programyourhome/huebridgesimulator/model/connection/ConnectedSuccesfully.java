package com.programyourhome.huebridgesimulator.model.connection;

import java.util.ArrayList;

import com.programyourhome.huebridgesimulator.model.connection.ConnectedSuccesfully.Wrapper;

/**
 * JSON DTO for the api response of connecting a new user.
 * The ArrayList<Wrapper> construct is needed to create the right type of JSON output, namely an array with one item.
 */
public class ConnectedSuccesfully extends ArrayList<Wrapper> implements HueBridgeResponse {

    private static final long serialVersionUID = 1L;

    public ConnectedSuccesfully(final String username) {
        this.add(new Wrapper(username));
    }

    public class Wrapper {

        private final Username success;

        public Wrapper(final String username) {
            this.success = new Username();
            this.success.username = username;
        }

        public Username getSuccess() {
            return this.success;
        }

        public class Username {
            private String username;

            public String getUsername() {
                return this.username;
            }
        }

    }

}
