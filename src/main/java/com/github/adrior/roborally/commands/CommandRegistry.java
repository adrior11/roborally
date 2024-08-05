package com.github.adrior.roborally.commands;

import com.github.adrior.roborally.commands.executors.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.adrior.roborally.commands.CommandPhase.*;

/**
 * Manages the registration and organization of all server commands.
 * This class provides a centralized point for setting up commands and their aliases,
 * facilitating efficient command retrieval and execution.
 * It uses a CommandManager to manage mappings of command names to command objects,
 * including permission checks.
 */
@Getter
@NoArgsConstructor
public class CommandRegistry {
    private final List<CommandEntry> commands = new ArrayList<>();

    /**
     * Initializes and registers all commands with their corresponding aliases.
     */
    void initializeCommands() {
        registerCommand("help", new HelpCommand(this),
                Set.of(), "?");

        registerCommand("move", new MoveCommand(),
                Set.of(UPGRADE_PHASE, PROGRAMMING_PHASE), "mv", "step", "walk");

        registerCommand("teleport", new TeleportCommand(),
                Set.of(UPGRADE_PHASE, PROGRAMMING_PHASE), "tp", "warp", "jump");

        registerCommand("rotate", new RotateCommand(),
                Set.of(UPGRADE_PHASE, PROGRAMMING_PHASE), "turn", "rot");

        registerCommand("drawdamage", new DrawDamageCommand(),
                Set.of(), "drawdmg", "dmgcard", "dmgdraw");

        registerCommand("shufflediscard", new ShuffleDiscardPileCommand(),
                Set.of(), "shuffle", "sd", "reshuffledeck");

        registerCommand("adjustenergy", new AdjustEnergyCommand(),
                Set.of(), "energy", "ae");

        registerCommand("advancecheckpoint", new AdvanceCheckpointCommand(),
                Set.of(SETUP_PHASE, UPGRADE_PHASE, PROGRAMMING_PHASE), "advancecp", "acp");

        registerCommand("reboot", new RebootCommand(),
                Set.of(UPGRADE_PHASE, PROGRAMMING_PHASE), "rb");

        registerCommand("resetgame", new ResetGameCommand(),
                Set.of(), "restartgame", "newgame", "rg");
    }


    /**
     * Registers a command along with its possible aliases.
     * @see <a href="https://docs.oracle.com/javase/tutorial/java/javaOO/arguments.html#varargs">Arbitrary Number of Arguments</a>
     *
     * @param commandName       The primary name of the command.
     * @param command           The command object.
     * @param commandAliases    Optional aliases through which the command can be invoked.
     */
    private void registerCommand(String commandName, ICommand command, Set<CommandPhase> validPhases, String... commandAliases) {
        commands.add(new CommandEntry(commandName, command, validPhases, Arrays.stream(commandAliases).collect(Collectors.toSet())));
    }


    /**
     * Retrieves a command based on its name or alias.
     *
     * @param commandName The name or alias of the command to retrieve.
     * @return The command object if found, otherwise null.
     */
    ICommand getCommand(String commandName) {
        return commands.stream()
                .filter(entry -> entry.primaryCommand().equals(commandName) || entry.aliases().contains(commandName))
                .map(CommandEntry::command)
                .findFirst()
                .orElse(null);
    }


    /**
     * Generates a help message listing all registered commands and their descriptions.
     *
     * @return A formatted string containing the help message.
     */
    public String getHelpMessage() {
        return commands.stream()
                .distinct()
                .map(entry -> String.format("/%s%s",
                        entry.primaryCommand(),
                        entry.aliases().isEmpty() ? "" : (" (Aliases: " + String.join(", ", entry.aliases()) + ")")))
                .collect(Collectors.joining("\n"));
    }


    /**
     * Retrieves the set of valid phases during which a command can be executed.
     *
     * @param commandName The name or alias of the command to retrieve the valid phases for.
     * @return The set of valid phases for the command, or an empty set if not found.
     */
    @NonNull Set<CommandPhase> getValidPhases(String commandName) {
        return commands.stream()
                .filter(entry -> entry.primaryCommand().equals(commandName) || entry.aliases().contains(commandName))
                .map(CommandEntry::validPhases)
                .findFirst()
                .orElse(Set.of());
    }
}
