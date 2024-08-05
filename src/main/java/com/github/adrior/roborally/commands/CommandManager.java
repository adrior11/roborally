package com.github.adrior.roborally.commands;

import com.github.adrior.roborally.core.game.GameManager;
import com.github.adrior.roborally.core.game.GameState;
import com.github.adrior.roborally.exceptions.InvalidCommandException;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.ServerCommunicationFacade;
import lombok.NonNull;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Singleton class for managing command execution based on incoming messages.
 *
 * <p> The CommandManager handles incoming messages, identifies the command, validates inputs,
 * and executes the corresponding commands or throws errors.
 */
public final class CommandManager {
    private static final AtomicReference<CommandManager> instance = new AtomicReference<>(); // Thread-safe instance

    @NonNull private final CommandRegistry registry;

    // Private constructor to prevent instantiation.
    private CommandManager() {
        this.registry = new CommandRegistry();
        this.registry.initializeCommands();
    }


    /**
     * Retrieves the singleton instance of the CommandManager class.
     *
     * @return the singleton instance of the CommandManager class
     */
    public static CommandManager getInstance() {
        if (null == instance.get()) {
            synchronized (CommandManager.class) {
                if (null == instance.get()) {
                    instance.set(new CommandManager());
                }
            }
        }
        return instance.get();
    }


    /**
     * Processes an incoming message to execute a command.
     * Checks if the command exists, validates inputs, and executes or displays errors.
     *
     * @param clientId      The clientId unique identifier of the client and corresponding player.
     * @param message       The message containing the command name and arguments.
     */
    public void processCommand(int clientId, @NonNull String message) {
        // Split the message into command name and arguments.
        String[] parts = message.trim().split("\\s+");
        String commandName = parts[0].substring(1); // Remove leading '/'
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        // Retrieve the command from the registry.
        ICommand command = registry.getCommand(commandName);

        if (null == command) {
            throw new InvalidCommandException(String.format(
                    "Unknown command '%s'. Type /help for assistance.", commandName));
        }

        // Assert if the command can be executed during the current phase.
        GameState currentPhase = GameManager.getInstance().getTurnManager().getCurrentPhase();
        Set<CommandPhase> validPhases = registry.getValidPhases(commandName);

        if (!validPhases.isEmpty() && validPhases.stream().noneMatch(phase -> phase.name().equals(currentPhase.name()))) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                    String.format("Command '%s' cannot be executed during the %s phase.", commandName, currentPhase)), clientId);
            return;
        }

        // Execute the command and catch any thrown exceptions.
        try {
            command.execute(clientId, args);
        } catch (Exception e) {
            throw new InvalidCommandException(String.format(
                    "Error executing command '%s': '%s'. Type /help for assistance.", commandName, e.getMessage()));
        }
    }
}
