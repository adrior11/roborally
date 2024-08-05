package com.github.adrior.roborally.exceptions;

/**
 * Custom exception for resource file operations.
 */
public class ResourceFileOperationException extends RuntimeException {
    public ResourceFileOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
