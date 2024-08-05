package com.github.adrior.roborally.server;

import com.google.gson.Gson;
import com.github.adrior.roborally.exceptions.InvalidMessageConfigurationException;
import com.github.adrior.roborally.message.Message.MessageType;
import com.github.adrior.roborally.message.handlers.IMessageHandler;
import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.utility.Config;
import lombok.Getter;
import lombok.NonNull;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ClientHandler manages individual client connections and communication for a {@link Server}.
 * It implements Runnable to be executed in a separate thread for each client connection,
 * handling all incoming and outgoing messages for that client.
 *
 * <p> The handler is responsible for:
 * - Reading messages from the client.
 * - Processing {@link Message} based on their type (e.g., HELLO_SERVER, SEND_CHAT, PLAY_CARD).
 * - Sending responses back to the client.
 */
public class ClientHandler implements Runnable {
    private static final long ALIVE_TIMEOUT = 3000; // 3-second timeout
    private final Timer aliveCheckTimer = new Timer(true); // Timer to check alive responses
    private final AtomicBoolean stopProcessing = new AtomicBoolean(false);
    private final ServerMessageHandlerRegistry handlerRegistry = new ServerMessageHandlerRegistry();
    private final Gson gson;
    private final Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    @Getter private final AtomicBoolean receivedAlive = new AtomicBoolean(true);
    @Getter private final Server server;


    /**
     * Constructs a new client handler for managing communication with a single client.
     *
     * @param socket The socket through which the client is connected.
     * @param server The {@link Server} managing this connection.
     */
    protected ClientHandler(Socket socket, Server server, Gson gson) {
        this.socket = socket;
        this.server = server;
        this.gson = gson;
    }


    /**
     * The entry point for the client handler thread.
     * Sets up input and output streams and processes incoming messages until the connection is closed.
     */
    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

            logWithClientId("Started");
            sendMessage(PredefinedServerMessages.helloClient(Config.getInstance().getProtocolVersion()));
            logWithClientId("Sent out HelloClient Message");
            processIncomingMessages();
        } catch (IOException e) {
            if (!stopProcessing.get()) {
                closeResources();
            }
        }
    }


    /**
     * Listens for messages from the client and processes each {@link Message} accordingly.
     * Continues to process messages until instructed to stop or an error occurs.
     *
     * <p> Receiving messages will trigger the associated handler given by their protocol type.
     *
     * @see IMessageHandler
     */
    private void processIncomingMessages() {
        try {
            String messageJson;
            while (!stopProcessing.get() && null != (messageJson = in.readLine())) {
                Message message = gson.fromJson(messageJson, Message.class);

                if (MessageType.ALIVE != message.messageType()) {
                    logWithClientId(String.format("Received %s: %s", message.messageType(), messageJson));
                }

                IMessageHandler<ClientHandler> handler = handlerRegistry.getHandler(message.messageType());

                if (null != handler) {
                    handleMessage(message, handler);
                } else {
                    logWithClientId("No handler registered for message type: " + message.messageType());
                    sendMessage(PredefinedServerMessages.error("Incorrect Message received"));
                }
            }
        } catch (Exception e) {
            if (!server.getIsShuttingDown().get()) {
                logWithClientId("Error reading client messages " + e.getMessage());
                closeResources();
            }
        }
    }


    /**
     * Handles the {@link Message} by delegating it to the appropriate {@link IMessageHandler}.
     *
     * @see InvalidMessageConfigurationException
     *
     * @param message the message to be handled
     * @param handler the handler responsible for processing the message
     */
    private void handleMessage(Message message, IMessageHandler<ClientHandler> handler) {
        try {
            handler.handle(message, this);
        } catch (InvalidMessageConfigurationException e) {
            logWithClientId("Invalid message configuration: " + e.getMessage());
            sendMessage(PredefinedServerMessages.error(e.getMessage()));
        }
    }


    /**
     * Sends a {@link Message} to the connected client.
     *
     * @param message The {@link Message} to send.
     */
    public void sendMessage(@NonNull Message message) {
        if (MessageType.ERROR == message.messageType()) {
            logWithClientId("Error sending message: " + message.messageBody());
        }

        out.println(gson.toJson(message));
    }


    /**
     * Sends an Alive message to the client.
     *
     * @param aliveMessage The Alive message to be sent.
     */
    public void sendAliveMessage(@NonNull Message aliveMessage) {
        sendMessage(aliveMessage);

        // Schedule a task to check if the alive message was acknowledged.
        aliveCheckTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!receivedAlive.get()) {
                    logWithClientId("No alive message received in reasonable time. Disconnecting...");
                    closeResources();           // Disconnect if not acknowledged.
                }
                receivedAlive.set(false);       // Reset the flag after the timeout occurs.
            }
        }, ALIVE_TIMEOUT);
    }

    /**
     * Closes all resources associated with the client, including streams and the client's socket.
     */
    public void closeResources() {
        if (stopProcessing.compareAndSet(false, true)) {
            closeQuietly(out, "OUT");
            closeQuietly(in, "IN");
            closeQuietly(socket, "SOCKET");
            server.removeClient(this);
        } else {
            logWithClientId("Resources already closed");
        }
    }


    /**
     * Attempts to close a resource quietly without throwing exceptions to the caller.
     *
     * @param resource The resource to be closed, which can be any objects that implements {@link AutoCloseable}.
     * @param resourceName The name of the resource, used for logging purposes to identify which resource is being closed.
     */
    private void closeQuietly(@NonNull AutoCloseable resource, String resourceName) {
        try {
            resource.close();
        } catch (Exception e) {
            logWithClientId(String.format("Error closing %s: %s", resourceName, e.getMessage()));
        }
    }


    /**
     * Logs a message with the client ID to the {@link Server}.
     *
     * @param message The message to be logged.
     */
    private void logWithClientId(String message) {
        ClientData clientData = server.getClients().get(this);
        String clientId = Optional.ofNullable(clientData).map(data -> String.valueOf(data.getClientId())).orElse("?");
        server.log(String.format("[ClientHandler] <%s> %s", clientId, message));
    }
}
