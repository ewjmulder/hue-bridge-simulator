package com.programyourhome.huebridgesimulator.model.connection;

/**
 * Type of error encountered.
 */
public enum ErrorType {

    UNAUTHORIZED_USER(1), 
    RESOURCE_NOT_AVAILABLE(3);

    private int code;

    private ErrorType(final int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

}
