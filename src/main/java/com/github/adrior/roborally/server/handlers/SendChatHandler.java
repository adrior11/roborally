package com.github.adrior.roborally.server.handlers;

import com.github.adrior.roborally.commands.CommandManager;
import com.github.adrior.roborally.core.game.GameManager;
import com.github.adrior.roborally.exceptions.InvalidCommandException;
import com.github.adrior.roborally.message.handlers.IMessageHandler;
import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.*;
import com.github.adrior.roborally.server.util.AssertionHelper;
import com.github.adrior.roborally.server.util.TypeCastingHelper;
import com.github.adrior.roborally.utility.Config;
import lombok.NonNull;

public class SendChatHandler implements IMessageHandler<ClientHandler> {

    private static final Config cfg = Config.getInstance();

    @Override
    public void handle(@NonNull Message message, @NonNull ClientHandler clientHandler) {
        if (!AssertionHelper.assertClientDataExists(clientHandler)) return;

        Server server = clientHandler.getServer();
        int from = server.getClients().get(clientHandler).getClientId();
        String chatMessage = message.messageBody().get("message").toString();

        // Assert if cheats are enabled and if the entered command message is valid.
        if (cfg.isCheatsEnabled() && chatMessage.startsWith("/") && GameManager.getInstance().getIsGameActive().get()) {
            try {
                CommandManager.getInstance().processCommand(from, chatMessage);
                return;
            } catch (InvalidCommandException ice) {
                clientHandler.sendMessage(PredefinedServerMessages.error(ice.getMessage()));
                return;
            }
        }

        int to = TypeCastingHelper.getIntFromMessage(clientHandler, message, "to");
        boolean isPrivate = (-1 != to);
        Message receivedChatMessage = PredefinedServerMessages.receivedChat(chatMessage, from, isPrivate);

        if (isPrivate) {
            server.forwardMessageToClient(receivedChatMessage, to, clientHandler);
        } else {
            ServerCommunicationFacade.broadcast(receivedChatMessage);
        }
    }
}
