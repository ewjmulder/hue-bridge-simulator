package com.programyourhome.huebridgesimulator.model.connection;

public enum ErrorType {

    USER_NOT_CONNECTED(101);

    private int code;

    private ErrorType(final int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

}
