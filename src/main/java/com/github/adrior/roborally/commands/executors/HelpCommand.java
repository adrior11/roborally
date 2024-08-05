package com.github.adrior.roborally.commands.executors;

import com.github.adrior.roborally.commands.CommandRegistry;
import com.github.adrior.roborally.commands.ICommand;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.ServerCommunicationFacade;
import lombok.AllArgsConstructor;
import lombok.NonNull;

/**
 * Command to provide help information about available commands.
 */
@AllArgsConstructor
public class HelpCommand implements ICommand {
    @NonNull private final CommandRegistry registry;

    @Override
    public void execute(int id, String[] args) {
        ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(String.format(
                "Available commands: %n%s", registry.getHelpMessage())), id);
    }
}
