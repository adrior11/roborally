package com.github.adrior.roborally.exceptions;

/**
 * Custom exception for invalid message configuration errors.
 * Thrown when a message contains invalid configuration parameters.
 */
public class InvalidMessageConfigurationException extends RuntimeException {
    public InvalidMessageConfigurationException(String message) {
        super(message);
    }
}
