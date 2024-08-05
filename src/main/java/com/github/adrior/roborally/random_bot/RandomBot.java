package com.github.adrior.roborally.random_bot;

import com.github.adrior.roborally.client.Client;
import com.github.adrior.roborally.core.tile.Tile;
import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.handlers.IMessageHandler;
import com.github.adrior.roborally.utility.GsonUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The RandomBot class extends the Client class and represents a client which randomly chooses programming cards.
 */
public class RandomBot extends Client {
    private static final Logger logger = Logger.getLogger(RandomBot.class.getName());

    private final RandomBotMessageHandlerRegistry handlerRegistry = new RandomBotMessageHandlerRegistry();
    private final ExecutorService executorService = Executors.newFixedThreadPool(30);

    @NonNull private static ExecutorService botExecutorService = Executors.newFixedThreadPool(6);

    @Getter private final List<RandomBotData> clients = new ArrayList<>();
    @Setter private boolean isLogging = true;

    @Getter @Setter private List<List<List<Tile>>> map;
    @Getter @Setter private boolean canStartBot = false;
    @Getter @Setter private String currentCard;

    @NonNull @Getter @Setter private AtomicInteger currentPhase = new AtomicInteger(0);

    /**
     * Constructs a RandomBot instance.
     *
     * @param host The host address of the server.
     * @param port The port number of the server.
     */
    public RandomBot(String host, int port) {
        super(host, port);
    }


    /**
     * Starts a RandomBot client on a separate thread.
     *
     * @param host The host address of the server.
     * @param port The port number of the server.
     */
    public static synchronized void startBot(String host, int port) {
        if (botExecutorService.isShutdown() || botExecutorService.isTerminated()) {
            botExecutorService = Executors.newFixedThreadPool(6);
        }

        botExecutorService.submit(() -> {
            RandomBot botClient = new RandomBot(host, port);
            botClient.setLogging(false);
            botClient.connect();
        });
    }

    /**
     * Reads messages from the server and processes them asynchronously.
     */
    @Override
    public void readMessages() {
        try {
            String line;
            while (null != (line = in.readLine())) {
                Message message = GsonUtil.getGson().fromJson(line, Message.class);

                executorService.submit(() -> {
                    IMessageHandler<RandomBot> handler = handlerRegistry.getHandler(message.messageType());

                    if (null != handler) handler.handle(message, this);
                });
            }
        } catch (Exception e) {
            if (!isDisconnecting.get()) {
                logger.severe("Error reading messages: " + e.getMessage());
            }
        }
    }


    /**
     * Finds a client by their clientId.
     *
     * @param id The clientId of the client to find.
     * @return The TestClientData instance corresponding to the client clientId, or null if not found.
     */
    public RandomBotData findClientById(int id) {
        return clients.stream()
                .filter(clientData -> clientData.getClientId() == id)
                .findFirst()
                .orElse(null);
    }


    /**
     * Stops a RandomBot thread for the given interval in milliseconds.
     *
     * @param milliseconds The time interval in milliseconds.
     */
    public void sleepMilliseconds(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            logError(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }


    /**
     * Logs an info message.
     *
     * @param message The error message to log.
     */
    public void log(String message) {
        if (isLogging) logger.log(Level.INFO, "<RandomBot> {0}", message);
    }


    /**
     * Logs an error message.
     *
     * @param message The error message to log.
     */
    public void logError(String message) {
        if (isLogging) logger.warning(message);
    }


    /**
     * Closes the RandomBot's resources including streams, the socket, and the alive check timer.
     */
    @Override
    public void closeResources() {
        super.closeClient();  // Call the parent method to ensure all resources are properly closed
        executorService.shutdownNow();  // Shut down the executor service
        botExecutorService.shutdownNow();  // Shut down the bot executor service
    }


    /**
     * The main method to start the RandomBot.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        Logger logger = Logger.getLogger(RandomBot.class.getName());
        Scanner scanner = new Scanner(System.in);

        logger.info("Enter host:");
        String host = scanner.nextLine().trim();

        logger.info("Enter port:");
        int port = -1;
        while (-1 == port) {
            try {
                port = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                logger.warning("Invalid port. Please enter a valid port number:");
            }
        }

        try {
            RandomBot client = new RandomBot(host, port);
            client.connect();
        } catch (Exception e) {
            logger.severe("Error initializing client: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}