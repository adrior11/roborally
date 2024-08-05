package com.github.adrior.roborally.message.utils;

import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.Message.MessageType;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Builder class for creating {@link Message} instances.
 * This class provides a fluent API to set the properties of a {@link Message} object
 * in a step-by-step process.
 */
public class MessageBuilder {
    private MessageType messageType;
    private final Map<String, Object> content = new HashMap<>();

    /**
     * Sets the type of the {@link Message}.
     *
     * @param messageType The type of the message, indicating how it should be processed.
     * @return The builder instance to chain configuration methods.
     */
    @NonNull public MessageBuilder setMessageType(MessageType messageType) {
        this.messageType = messageType;
        return this;
    }

    /**
     * Adds a key-value pair to the {@link Message} body.
     *
     * @param key The key for the data entry.
     * @param value The value for the data entry.
     * @return The builder instance to chain configuration methods.
     */
    @NonNull public MessageBuilder addContent(String key, Object value) {
        this.content.put(key, value);
        return this;
    }

    /**
     * Constructs a new {@link Message} instance using the current settings of the builder.
     *
     * @return A new {@link Message} object configured with the builder's current settings.
     */
    @NonNull public Message build() {
        return new Message(messageType, content);
    }
}
