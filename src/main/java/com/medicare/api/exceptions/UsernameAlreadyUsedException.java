package com.medicare.api.exceptions;

public class UsernameAlreadyUsedException extends RuntimeException {

    public UsernameAlreadyUsedException(String message) {
        super(message);
    }
}
