package com.github.adrior.roborally.server.handlers;

import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.handlers.IMessageHandler;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.*;
import com.github.adrior.roborally.server.util.AssertionHelper;
import com.github.adrior.roborally.server.util.TypeCastingHelper;
import lombok.NonNull;

public class PlayerValuesHandler implements IMessageHandler<ClientHandler> {

    @Override
    public void handle(@NonNull Message message, @NonNull ClientHandler clientHandler) {
        if (!AssertionHelper.assertClientDataExists(clientHandler)) return;
        if (!AssertionHelper.assertGameIsNotActive(clientHandler)) return;

        Server server = clientHandler.getServer();
        ClientData retrievedClientData = server.getClients().get(clientHandler);
        int clientId = retrievedClientData.getClientId();

        String username = message.messageBody().get("name").toString();
        int figure = TypeCastingHelper.getIntFromMessage(clientHandler, message, "figure");

        // Assert if 6 clients have provided their values.
        if (6 == server.countValidClients()) {
            clientHandler.sendMessage(PredefinedServerMessages.error(
                    "The lobby is already full. Disconnecting..."));
            clientHandler.closeResources();
            return;
        }

        // Check if the figure is available.
        if (!server.isFigureAvailable(figure)) {
            clientHandler.sendMessage(PredefinedServerMessages.error(
                    "Selected figure is not available. Disconnecting..."));
            clientHandler.closeResources();
            return;
        }

        // Store the new data in the respective BackendClientData.
        retrievedClientData.setUsername(username);
        retrievedClientData.setFigure(figure);

        // Inform other players about the newly connected client.
        ServerCommunicationFacade.log(String.format(
                "<Handler> Player registered for %s. Username: %s Figure: %s", clientId, username, figure));
        ServerCommunicationFacade.broadcast(PredefinedServerMessages.playerAdded(clientId, username, figure));
    }
}