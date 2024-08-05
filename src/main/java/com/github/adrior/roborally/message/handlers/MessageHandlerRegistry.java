package com.github.adrior.roborally.message.handlers;

import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.Message.MessageType;

import java.util.HashMap;
import java.util.Map;

/**
 * The MessageHandlerRegistry class maintains a registry of {@link Message} handlers
 * for different message types. It provides methods to register handlers and
 * retrieve handlers based on the {@link MessageType}. This class is generic and can be
 * used for both client and server by parameterizing the type.
 *
 * @see IMessageHandler
 *
 * @param <T> The type of the handler that processes the message.
 */
public class MessageHandlerRegistry <T> {
    private final Map<MessageType, IMessageHandler<T>> handlers = new HashMap<>();


    /**
     * Registers a handler for a specific message type.
     *
     * @param type    The message type for which the handler is being registered.
     * @param handler The handler to be registered for the specified message type.
     */
    protected void registerHandler(MessageType type, IMessageHandler<T> handler) {
        handlers.put(type, handler);
    }


    /**
     * Retrieves the handler registered for the specified message type.
     *
     * @param type The message type for which the handler is being retrieved.
     * @return The handler registered for the specified message type, or null if no handler is registered.
     */
    public IMessageHandler<T> getHandler(MessageType type) {
        return handlers.get(type);
    }
}