package com.itblee.exception;

public class UserExistException extends ChatAppException {
    public UserExistException(String message) {
        super(message);
    }
}
