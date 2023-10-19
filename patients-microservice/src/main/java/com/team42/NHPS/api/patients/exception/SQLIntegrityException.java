package com.team42.NHPS.api.patients.exception;

public class SQLIntegrityException extends RuntimeException{
    private String message;

    public SQLIntegrityException(String message) {
        super(message);
    }

    public String getMessage() {
        return message;
    }

}
