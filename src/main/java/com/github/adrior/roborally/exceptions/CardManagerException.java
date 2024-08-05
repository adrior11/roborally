package com.github.adrior.roborally.exceptions;

/**
 * Custom exception for card manager errors.
 */
public class CardManagerException extends RuntimeException {
    public CardManagerException(String message) {
        super(message);
    }
}
