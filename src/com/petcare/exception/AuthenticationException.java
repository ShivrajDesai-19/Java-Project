package com.petcare.exception;

/**
 * Unit 3: Custom Exception for authentication & role authorization errors
 */
public class AuthenticationException extends PetCareException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
