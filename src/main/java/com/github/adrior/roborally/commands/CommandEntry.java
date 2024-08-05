package com.github.adrior.roborally.commands;

import java.util.Set;

/**
 * Represents an entry for a command in the command registry.
 * Each CommandEntry encapsulates information about a command,
 * including its primary name, description, command implementation,
 * valid phases and optional aliases.
 *
 * @param primaryCommand The primary name of the command.
 * @param command The command implementation.
 * @param validPhases The set of valid phases for the command.
 * @param aliases The optional aliases for the command.
 */
record CommandEntry(String primaryCommand, ICommand command, Set<CommandPhase> validPhases, Set<String> aliases) {}
