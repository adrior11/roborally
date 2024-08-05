package com.github.adrior.roborally.exceptions;

/**
 * Custom exception for invalid game states.
 */
public class InvalidGameStateException extends RuntimeException {
    public InvalidGameStateException(String message) {
        super(message);
    }
}
