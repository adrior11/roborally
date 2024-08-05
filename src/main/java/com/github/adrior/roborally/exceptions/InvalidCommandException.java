package com.github.adrior.roborally.exceptions;

/**
 * Custom exception for invalid command errors.
 */
public class InvalidCommandException extends RuntimeException {
    public InvalidCommandException(String message) {
        super(message);
    }
}
