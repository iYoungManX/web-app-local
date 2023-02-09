package com.csye6225.Exception.UserException;

public class InvalidUpdateException extends RuntimeException {
    public InvalidUpdateException(String message) {
        super(message);
    }
}
