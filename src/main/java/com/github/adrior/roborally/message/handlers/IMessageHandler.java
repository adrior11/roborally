package com.github.adrior.roborally.message.handlers;

import com.github.adrior.roborally.message.Message;

/**
 * The IMessageHandler interface defines the contract for handling messages of a specific type.
 * Implementations of this interface are responsible for processing messages
 * and performing actions based on the {@link Message} content.
 *
 * @param <T> The type of the handler that processes the message.
 */
public interface IMessageHandler<T> {

    /**
     * Handles a message of a specific type.
     *
     * @param message The message to be handled.
     * @param handler The handler that processes the message.
     */
    void handle(Message message, T handler);
}
