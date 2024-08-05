package com.github.adrior.roborally.exceptions;

/**
 * Custom exception for configuration loading errors.
 */
public class ConfigurationLoadException extends RuntimeException {
    public ConfigurationLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
