package com.csye6225.Exception;

public class RepeatEmailException extends RuntimeException {
    public RepeatEmailException(String message) {
        super(message);
    }
}
