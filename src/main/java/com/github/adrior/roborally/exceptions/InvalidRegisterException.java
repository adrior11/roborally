package com.github.adrior.roborally.exceptions;

/**
 * Custom exception for invalid register operations.
 */
public class InvalidRegisterException extends RuntimeException {
    public InvalidRegisterException(String message) {
        super(message);
    }
}
