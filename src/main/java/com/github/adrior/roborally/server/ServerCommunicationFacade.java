package com.github.adrior.roborally.server;

import com.github.adrior.roborally.message.Message;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The ServerCommunicationFacade class provides a simplified interface for centralized
 * broadcasting, logging, removing AI clients, and client-specific {@link Message} sending functionalities,
 * acting as a Facade for these operations. This utility class allows other components
 * of the backend to broadcast messages to all clients, send messages to specific clients,
 * and log messages without needing direct access to the underlying mechanisms of the server.
 */
@UtilityClass
public final class ServerCommunicationFacade {
    private static Consumer<Message> broadcaster = null;
    private static Consumer<String> logger = null;
    private static BiConsumer<Message, Integer> sendMessage = null;
    private static Consumer<Void> kickAIClients = null;

    private static final Logger defaultLogger = Logger.getLogger(ServerCommunicationFacade.class.getName());

    /**
     * Sets the broadcaster consumer that will be used to broadcast messages.
     *
     * @param broadcaster the consumer that handles broadcasting messages to all clients.
     */
    static void setBroadcaster(Consumer<Message> broadcaster) {
        ServerCommunicationFacade.broadcaster = broadcaster;
    }


    /**
     * Sets the logger consumer that will be used to log messages.
     *
     * @param logger the consumer that handles logging messages.
     */
    static void setLogger(Consumer<String> logger) {
        ServerCommunicationFacade.logger = logger;
    }


    /**
     * Sets the sendMessage consumer that will be used to send messages to specific clients.
     *
     * @param sendMessage the consumer that handles sending messages to a specific client.
     */
    static void setSendMessage(BiConsumer<Message, Integer> sendMessage) {
        ServerCommunicationFacade.sendMessage = sendMessage;
    }


    /**
     * Sets the kickAIClients consumer that will be used to kick AI clients.
     *
     * @param kickAIClients the consumer that handles kicking AI clients.
     */
    static void setKickAIClients(Consumer<Void> kickAIClients) {
        ServerCommunicationFacade.kickAIClients = kickAIClients;
    }


    /**
     * Broadcasts a message to all game clients.
     *
     * @param message the message to be broadcast to all clients.
     */
    public static void broadcast(@NonNull Message message) {
        if (null != broadcaster) {
            broadcaster.accept(message);
        } else if (defaultLogger.isLoggable(Level.INFO)) {
            defaultLogger.info(String.format("Broadcasting: %s: %s", message.messageType(), message.messageBody()));
        }
    }


    /**
     * Sends a message to a specific client.
     *
     * @param message  the message to be sent to the client.
     * @param clientId the unique clientId of the client to whom the message should be sent.
     */
    public static void sendMessage(@NonNull Message message, int clientId) {
        if (null != sendMessage) {
            sendMessage.accept(message, clientId);
        } else if (defaultLogger.isLoggable(Level.INFO)) {
            defaultLogger.info(String.format("Sending message to %d: %s: %s", clientId, message.messageType(), message.messageBody()));
        }
    }


    /**
     * Logs a message.
     *
     * @param message the message to be logged.
     */
    public static void log(String message) {
        if (null != logger) {
            logger.accept(String.format("[Logging] %s", message));
        } else if (defaultLogger.isLoggable(Level.INFO)) {
            defaultLogger.info(String.format("Logging: %s", message));
        }
    }


    /**
     * Kicks all AI clients.
     */
    public static void kickAIClients() {
        if (null != kickAIClients) {
            kickAIClients.accept(null);
        } else if (defaultLogger.isLoggable(Level.INFO)) {
            defaultLogger.info("Kicking AI clients");
        }
    }
}
