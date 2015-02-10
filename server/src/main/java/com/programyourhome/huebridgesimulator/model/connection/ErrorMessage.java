package com.programyourhome.huebridgesimulator.model.connection;

public class ErrorMessage implements HueBridgeResponse {

    private final Error error;

    public ErrorMessage(final ErrorType type, final String address, final String description) {
        this.error = new Error();
        this.error.type = type.getCode();
        this.error.address = address;
        this.error.description = description;
    }

    public class Error {
        private int type;
        private String address;
        private String description;

        public int getType() {
            return this.type;
        }

        public String getAddress() {
            return this.address;
        }

        public String getDescription() {
            return this.description;
        }
    }
}
