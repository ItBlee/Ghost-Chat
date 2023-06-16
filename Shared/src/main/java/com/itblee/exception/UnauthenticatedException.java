package com.itblee.exception;

public class UnauthenticatedException extends RuntimeException{

	public UnauthenticatedException() {
	}

	public UnauthenticatedException(String message) {
		super(message);
	}

	public UnauthenticatedException(Throwable cause) {
		super(cause);
	}

}
