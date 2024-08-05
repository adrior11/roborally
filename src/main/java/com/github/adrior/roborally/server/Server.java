package com.github.adrior.roborally.server;

import com.google.gson.Gson;
import com.github.adrior.roborally.core.game.GameManager;
import com.github.adrior.roborally.core.map.AvailableCourses;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.random_bot.RandomBot;
import com.github.adrior.roborally.utility.Config;
import com.github.adrior.roborally.utility.GsonUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


/**
 * The Server class manages network connections, client interactions, and core server operations.
 * It supports operations such as starting the server, accepting client connections, broadcasting messages,
 * and shutting down cleanly. It is designed to handle multiple client connections concurrently using a thread pool.
 *
 * @see ClientHandler
 * @see ServerCommunicationFacade
 * @see ClientData
 * @see Message
 */
public class Server {
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private final ExecutorService clientThreadPool = Executors.newFixedThreadPool(100);
    private final AtomicInteger clientIdGenerator = new AtomicInteger(1);
    private final ConcurrentLinkedQueue<Integer> availableClientIds = new ConcurrentLinkedQueue<>();
    private final Timer aliveTimer = new Timer(true);
    private final Gson gson = GsonUtil.getGson();
    private final int minPlayers;                    // Sets the number of players needed to start a game
    private final int port;                                     // Can be null for testing.
    private ServerSocket serverSocket;
    private boolean isRunning = false;                                      // Flag for testing.

    @NonNull private final String sessionId;
    @Getter private final AtomicBoolean isShuttingDown = new AtomicBoolean(false);
    @NonNull @Getter @Setter private ConcurrentHashMap<ClientHandler, ClientData> clients = new ConcurrentHashMap<>();

    /**
     * Constructs a Server with a specific port.
     *
     * @param port The port number on which the server will listen for incoming connections.
     * @param minPlayers The minimum number of players (2-6) needed to start a game.
     */
    public Server(int port, int minPlayers) {
        this.port = port;
        this.minPlayers = minPlayers;
        this.sessionId = generateSessionId();
    }

    /**
     * Starts the server operations including setting up the server socket and beginning to listen for clients.
     * It also adds a shutdown hook to ensure the server shuts down cleanly when the runtime shuts down.
     */
    public void start() {
        setupServerCommunicationFacade();
        configureLogging();

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown, "Shutdown Hook"));
        GameManager.getInstance().setMinPlayers(minPlayers);
        log("[Server] Server started on protocol: " + Config.getInstance().getProtocolVersion());
        log("[Server] Cheats enabled: " + Config.getInstance().isCheatsEnabled());
        setupServerSocket();
    }


    /**
     * Sets up the broadcasting and logging utilities via the {@link ServerCommunicationFacade}.
     */
    private void setupServerCommunicationFacade() {
        ServerCommunicationFacade.setBroadcaster(this::broadcast);
        ServerCommunicationFacade.setLogger(this::log);
        ServerCommunicationFacade.setSendMessage(this::sendMessageToClient);
        ServerCommunicationFacade.setKickAIClients(_ -> kickAIClients());
    }


    /**
     * Configures the logging mechanism based on the configuration settings via the {@link Config}.
     * If file logging is enabled, it sets up a FileHandler to log messages to a file.
     */
    private void configureLogging() {
        Config config = Config.getInstance();
        if (config.isSavingLog()) {
            try {
                String logFileName = "server_" + sessionId + ".log";
                File logFile = new File(logFileName);

                log("File logging has been enabled. Please refer to the log file for more information.");
                log("Log file created at: " + logFile.getAbsolutePath());

                FileHandler fileHandler = new FileHandler(logFileName, true);
                fileHandler.setFormatter(new SimpleFormatter());

                logger.addHandler(fileHandler);
                logger.setUseParentHandlers(false);  // Disable console logging
            } catch (IOException e) {
                logger.severe("Failed to set up file logging: " + e.getMessage());
            }
        }
    }


    /**
     * Sets up the ServerSocket and starts listening for incoming client connections.
     */
    private void setupServerSocket() {
        log("[Server] Setting up server socket");
        try {
            serverSocket = new ServerSocket(port);
            isRunning = true;
            startAliveBroadcast();
            listenForClients();
        } catch (IOException | IllegalArgumentException e) {
            log(String.format("Failed to start server socket on port %s: %s", port, e.getMessage()));
            isRunning = false;
        }
    }


    /**
     * Continuously listens for new clients and handles each client connection in a separate thread.
     */
    private void listenForClients() {
        log("[Server] Listening for clients");
        try {
            while (!serverSocket.isClosed()) {
                if (!GameManager.getInstance().getIsGameActive().get()) {
                    Socket clientSocket = serverSocket.accept();
                    log("[Server] Client connected");
                    ClientHandler clientHandler = new ClientHandler(clientSocket, this, gson);
                    clientThreadPool.submit(clientHandler);
                }
            }
        } catch (IOException e) {
            if (serverSocket.isClosed()) {      // Triggered when the socket closes on shutdown.
                log("[Server] Server socket closed, stopping client listener");
            } else {
                logger.severe("Socket exception while accepting client connection: " + e.getMessage());
            }
        }
    }


    /**
     * Broadcasts a {@link Message} to all connected clients.
     *
     * @param message The message to be broadcast.
     */
    protected void broadcast(@NonNull Message message) {
        log(String.format("[Server] Broadcasting message: %s -> %s",
                message.messageType(),
                message.messageBody().toString()));
        clients.keySet().forEach(client -> client.sendMessage(message));
    }


    /**
     * Sends a {@link Message} to a specific client identified by their client clientId.
     * If the client is not found, log an error message.
     *
     * @param message The message to be sent to the client.
     * @param clientId The unique clientId of the client to whom the message should be sent.
     */
    private void sendMessageToClient(@NonNull Message message, int clientId) {
        ClientHandler clientHandler = findClientHandlerById(clientId);
        if (null != clientHandler) {
            clientHandler.sendMessage(message);
        } else {
            log("[Server] Error sending message to unknown client: " + clientId + ": " + message.messageType());
        }
    }


    /**
     * Sends a {@link Message} to a specific client.
     * Primarily used for handling private chat messages.
     *
     * @param clientId The clientId of the client to send the message to.
     * @param message  The message to be sent.
     */
    public void forwardMessageToClient(@NonNull Message message, int clientId, @NonNull ClientHandler sender) {
        ClientHandler recipientClient = findClientHandlerById(clientId);
        if (null != recipientClient) {
            log("[Server] Sending message to client " + clientId + " from " + clients.get(sender).getClientId());
            recipientClient.sendMessage(message);
        } else {
            log("[Server] Error sending message to client " + clientId + " from " + clients.get(sender).getClientId());
            sender.sendMessage(PredefinedServerMessages.error("Client with clientId " + clientId + " not found"));
        }
    }


    /**
     * Starts the periodic broadcast of the Alive message to all connected clients.
     */
    private void startAliveBroadcast() {
        aliveTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                clients.keySet().forEach(client -> client.sendAliveMessage(PredefinedServerMessages.alive()));
            }
        }, 0, 5000); // Schedule to run every 5 seconds
    }


    /**
     * Generates a unique session ID based on the current date and time, and a random {@link UUID}.
     *
     * @return A unique session ID string.
     */
    @NonNull private String generateSessionId() {
        return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS").format(new Date()) + "_" + UUID.randomUUID();
    }


    /**
     * Checks if the server is currently running.
     * This method returns the status of the server based on the {@code isRunning} flag,
     * which is set to true when the server successfully starts and set to false upon shutdown.
     *
     * @return true if the server is currently running, false otherwise.
     */
    public boolean isRunning() {
        return isRunning;
    }


    /**
     * Adds a new client to the set of managed {@link ClientHandler}.
     *
     * @param client The client handler to add.
     */
    public synchronized void addClient(@NonNull ClientHandler client) {
        int clientId;
        if (!availableClientIds.isEmpty()) {
            clientId = availableClientIds.poll();
        } else {
            clientId = clientIdGenerator.getAndIncrement();
        }
        log("[Server] Adding client: " + clientId);

        ClientData clientData = new ClientData();
        clientData.setClientId(clientId);
        clients.put(client, clientData);
    }


    /**
     * Removes a client from the set of managed {@link ClientHandler}.
     * If a game is active it will also remove the associated
     * {@link Player} of the client via the {@link GameManager}.
     *
     * <p> If the removed player was currently selecting a course
     * from the {@link AvailableCourses} it will pass the task over,
     * to the next connected non AI player
     *
     * @param clientHandler The {@link ClientHandler} to remove.
     */
    void removeClient(@NonNull ClientHandler clientHandler) {
        ClientData clientData = clients.get(clientHandler);

        if (null == clientData) {
            log("[Server] Connection refused");
            return;
        }

        int clientId = clientData.getClientId();
        clients.remove(clientHandler);

        log("[Server] Removing client: " + clientId);
        availableClientIds.offer(clientData.getClientId());

        GameManager gameManager = GameManager.getInstance();
        synchronized (gameManager) {
            if (gameManager.getIsGameActive().get()) {
                Player player = gameManager.getPlayerByID(clientId);

                if (null == player) {
                    log(String.format("[Server] Client %s has already been removed from the GameManager", clientId));
                    return;
                }

                gameManager.removePlayer(clientId);

                // Pass the right to choose a map to the next player during the lobby phase.
                if (player.flags().isSelectingMap()
                        && !gameManager.getPlayers().isEmpty()
                        && null != gameManager.getFirstConnectedNonAIPlayer()
                        && !gameManager.getIsGameActive().get()) {
                    Player firstConnectPlayer = gameManager.getFirstConnectedNonAIPlayer();
                    firstConnectPlayer.flags().setSelectingMap(true);

                    ServerCommunicationFacade.sendMessage(PredefinedServerMessages.selectMap(
                                    AvailableCourses.getFormattedNames()),
                            firstConnectPlayer.clientId());
                }

                // Reset the game if it's running and the count of players is less or equals 1.
                if (1 == gameManager.getPlayers().size()) {
                    gameManager.endGame(gameManager.getPlayers().getFirst().clientId());
                } else if (gameManager.getPlayers().isEmpty()) {
                    gameManager.resetGame();
                }
            } else {
                if (clients.isEmpty()) {
                    gameManager.resetGame();
                }
            }
        }

        log(String.format("[Server] Successfully removed client: %s", clientId));
        log(String.format("[Server] Connected clients: %s", clients.size()));
        broadcast(PredefinedServerMessages.connectionUpdate(clientId, false, "Remove"));
    }


    /**
     * Removes all connected AI clients from the server marked with the {@code isAI} flag.
     */
    private void kickAIClients() {
        log("[Server] Kicking AI clients");
        clients.forEach((key, value) -> {
            if (value.isAI()) removeClient(key);
        });
    }


    /**
     * Finds a connected client by their unique clientId.
     *
     * @param clientId The unique clientId of the client.
     * @return The found {@link ClientHandler} or null if no such client exists.
     */
    private ClientHandler findClientHandlerById(int clientId) {
        return clients.entrySet().stream()
                .filter(entry -> entry.getValue().getClientId() == clientId)
                .map(Entry::getKey)
                .findFirst()
                .orElse(null);
    }


    /**
     * Counts the number of {@link ClientData} instances that have a figure value not equal to -1.
     *
     * @return the count of BackendClientData instances with a valid figure value.
     */
    public int countValidClients() {
        return (int) clients.values().stream()
                .filter(clientData -> 0 <= clientData.figure && 5 >= clientData.figure)
                .count();
    }


    /**
     * Checks if a figure value is available among all clients.
     *
     * @param figure the figure value to check, must be in the interval [0, 5]
     * @return {@code true} if the figure is available, {@code false} otherwise
     */
    public boolean isFigureAvailable(int figure) {
        if (0 > figure || 5 < figure) return false;
        return clients.values().stream().noneMatch(data -> data.getFigure() == figure);
    }


    /**
     * Logs a message.
     *
     * @param message The message to be logged.
     */
    public void log(String message) {
        logger.info(message);
    }


    /**
     * Shuts down the server and cleans up resources, including closing server socket and client connections.
     * Also broadcasts a shutdown message to all connected clients, forcing them to disconnect.
     */
    public void shutdown() {
        if (null != serverSocket && !serverSocket.isClosed() && isShuttingDown.compareAndSet(false, true)) {
            logger.info("Shutting down server. Cleaning up resources...");

            clients.keySet().forEach(ClientHandler::closeResources);
            clients.clear();

            closeResources();
            aliveTimer.cancel();

            isRunning = false;
            isShuttingDown.set(false);

            logger.info("Cleanup complete");
        }
    }


    /**
     * Closes the server socket and shuts down the client thread pool.
     */
    private void closeResources() {
        try {
            if (null != serverSocket && !serverSocket.isClosed()) serverSocket.close();
            clientThreadPool.shutdown();
            logger.info("Server has been stopped");
        } catch (IOException e) {
            logger.severe("Could not close server socket: " + e.getMessage());
        }
    }


    /**
     * The main method to start the server.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        Logger logger = Logger.getLogger(RandomBot.class.getName());
        Scanner scanner = new Scanner(System.in);

        logger.info("Enter port:");
        int port = -1;
        while (-1 == port) {
            try {
                port = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                logger.warning("Invalid port. Please enter a valid port number:");
            }
        }

        logger.info("Enter min players:");
        int minPlayers = -1;
        while (-1 == minPlayers) {
            try {
                minPlayers = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                logger.warning("Invalid port. Please enter a valid port number:");
            }
        }

        try {
            Server server = new Server(port, minPlayers);
            server.start();
        } catch (Exception e) {
            logger.severe("Error initializing server: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}
