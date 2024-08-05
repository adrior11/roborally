package com.github.adrior.roborally.exceptions;

/**
 * Custom exception for invalid tile configurations.
 */
public class InvalidTileConfigurationException extends RuntimeException {
    public InvalidTileConfigurationException(String message) {
        super(message);
    }
}
