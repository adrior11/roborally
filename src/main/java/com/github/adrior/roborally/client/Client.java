// This Client class is responsible for managing the network operations
// for communicating with the game server, including connecting, sending,
// and receiving messages. However, this client is not working in its current
// state because it lacks controls and connectivity to a GUI/TUI. The random bot,
// which inherits from this client class, works because it bypasses the need
// for a GUI/TUI and directly handles network operations.

package com.github.adrior.roborally.client;

import com.google.gson.Gson;
import com.github.adrior.roborally.message.Message.MessageType;
import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.utils.PredefinedClientMessages;
import com.github.adrior.roborally.utility.Config;
import com.github.adrior.roborally.utility.GsonUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Client manages the network operations for communicating with the chat server.
 * This includes connecting to the server, sending messages, receiving messages, and handling disconnections.
 *
 * <p> It uses a socket for network communication, BufferedReader and PrintWriter for message input and output,
 * and runs message reading in a separate thread to keep the UI responsive.
 */
public class Client {
    private static final Logger logger = Logger.getLogger(Client.class.getName());

    protected static final long ALIVE_TIMEOUT = 7000;         // 7-second timeout
    protected static final long ALIVE_CHECK_INTERVAL = 5000;  // Check every 5 seconds

    // private final ClientMessageHandlerRegistry handlerRegistry = new ClientMessageHandlerRegistry();
    private final AtomicBoolean receivedAlive = new AtomicBoolean(true);
    private final Gson gson = GsonUtil.getGson();

    protected final Timer aliveCheckTimer = new Timer(true);
    protected final AtomicBoolean isDisconnecting = new AtomicBoolean(false);
    protected Socket socket;
    protected PrintWriter out;
    protected BufferedReader in;

    @Setter private Consumer<Message> onMessageReceived;

    @Getter protected final String host;
    @Getter protected final int port;
    @Getter protected final AtomicBoolean isConnected = new AtomicBoolean(false);

    @Getter @Setter private Integer clientId;
    @Setter @Getter private String username;
    @Getter @Setter private int figureId;

    /**
     * Constructs a new Client with specified connection details.
     *
     * @param host The server's hostname or IP address.
     * @param port The port number on which the server is listening to.
     */
    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }


    /**
     * Attempts to establish a connection with the server. Initializes streams and starts a new thread to read messages.
     */
    public void connect() {
        if (isConnected.get()) return;
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

            isConnected.set(true);
            new Thread(this::readMessages).start();
            startAliveCheck();
        } catch (IOException e) {
            logger.warning("Could not connect to [" + host + ":" + port + "]: " + e.getMessage());
            closeClient();
        }
    }


    /**
     * Starts a periodic check to ensure the client is receiving alive messages from the server.
     * If an alive message is not received within a specific timeout, the client is reset.
     */
    private void startAliveCheck() {
        aliveCheckTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!isConnected.get()) {
                    aliveCheckTimer.cancel();
                    return;
                }

                if (!receivedAlive.get()) {
                    logger.warning("No alive message received in reasonable time. Resetting client...");
                    closeClient();
                }
                receivedAlive.set(false);
            }
        }, ALIVE_CHECK_INTERVAL, ALIVE_TIMEOUT);
    }


    /**
     * Reads messages continuously from the server until disconnection.
     * Processes each message using the provided {@code onMessageReceived} consumer.
     * If the GUI is not available and the server sends a disconnect or shutdown message,
     * the disconnection is handled internally.
     */
    protected void readMessages() {
        try {
            String line;
            while (!isDisconnecting.get() && isConnected.get() && null != (line = in.readLine())) {
                Message message = gson.fromJson(line, Message.class);

                if (MessageType.ALIVE != message.messageType()
                        && logger.isLoggable(Level.INFO)
                        && !Config.getInstance().isSavingLog()) {
                    logger.info(String.format("Received: %s", line));
                }

                /*
                IMessageHandler<Client> handler = handlerRegistry.getHandler(message.messageType());

                if (null != handler) {
                    handler.handle(message, this);
                } else {
                    logger.warning("No handler registered for message type: " + message.messageType());
                }
                 */
            }
        } catch (Exception e) {
            if (!isDisconnecting.get()) {
                logger.severe("Error reading messages: " + e.getMessage());
            }
        }
    }


    /**
     * Sends a chat {@link Message} to the server.
     * Constructs a CHAT type {@link Message} with the provided chat message.
     *
     * @param message The chat {@link Message} input by the user.
     */
    public void sendChatMessage(String message) {
        sendMessage(PredefinedClientMessages.sendChat(message, -1));
    }


    /**
     * Sends a private chat {@link Message} to a specified client.
     * Constructs a SEND_CHAT type {@link Message} with the provided chat message and recipient ID.
     *
     * @param message The chat message input by the user.
     * @param to The ID of the recipient client.
     */
    public void sendPrivateChatMessage(String message, int to) {
        sendMessage(PredefinedClientMessages.sendChat(message, to));
    }


    /**
     * Sends a message to the server. This method serializes the {@link Message} object
     * to JSON format and writes it to the output stream.
     *
     * @param message The {@link Message} object to be sent to the server.
     */
    public void sendMessage(Message message) {
        out.println(gson.toJson(message));
    }


    /**
     * Handles the immediate closure of the client by closing all resources.
     * This method is invoked internally during disconnection, ensuring that all client-related resources are released.
     * It updates the connection status flags and notifies the GUI, if available, about the disconnection event.
     */
    public void closeClient() {
        if (isDisconnecting.compareAndSet(false, true)) {
            closeResources();

            aliveCheckTimer.cancel();

            isConnected.set(false);
            isDisconnecting.set(false);
        }
    }


    /**
     * Closes the client's resources including streams and the socket.
     */
    protected void closeResources() {
        try {
            if (null != in) in.close();
            if (null != out) out.close();
            if (null != socket) socket.close();
        } catch (IOException e) {
            logger.severe("Error closing resources: " + e.getMessage());
        }
    }


    /**
     * Sets the status of the received alive flag.
     *
     * @param receivedAlive The new status of the received alive flag.
     */
    public void setReceivedAlive(boolean receivedAlive) {
        this.receivedAlive.set(receivedAlive);
    }
}

