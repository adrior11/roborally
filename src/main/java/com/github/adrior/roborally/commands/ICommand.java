package com.github.adrior.roborally.commands;

/**
 * Represents a command within the application that can be executed.
 * This interface defines the methods required for executing a command
 * and retrieving its description, which helps in implementing various
 * functionalities dynamically based on the command pattern.
 */
@FunctionalInterface
public interface ICommand {

    /**
     * Executes the command with the given arguments using the provided client handler.
     * This method is responsible for performing the command's specific action
     * and managing its effect on the application's state or the client's state.
     *
     * @param id    The id representing the unique identifier of the connected client
     *              and his corresponding player needed for the flexible execution.
     * @param args  An array of strings representing the arguments passed to the command.
     *              These arguments allow for flexible command execution and parameterization.
     */
    void execute(int id, String[] args);
}
