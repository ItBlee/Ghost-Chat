package com.itblee.exception;

public class InvalidUsernameException extends BadRequestException {
    public InvalidUsernameException(String message) {
        super(message);
    }
}
