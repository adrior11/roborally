package com.github.adrior.roborally.server.handlers;

import com.github.adrior.roborally.core.game.GameManager;
import com.github.adrior.roborally.message.handlers.IMessageHandler;
import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.ClientData;
import com.github.adrior.roborally.server.ClientHandler;
import com.github.adrior.roborally.server.Server;
import com.github.adrior.roborally.server.ServerCommunicationFacade;
import com.github.adrior.roborally.server.util.TypeCastingHelper;
import com.github.adrior.roborally.utility.Config;
import lombok.NonNull;

import java.util.List;

public class HelloServerHandler implements IMessageHandler<ClientHandler> {

    private static final Config cfg = Config.getInstance();

    @Override
    public void handle(@NonNull Message message, @NonNull ClientHandler clientHandler) {
        GameManager gameManager = GameManager.getInstance();

        // Prevent connections after the game has already started.
        if (gameManager.getIsGameActive().get()) {
            ServerCommunicationFacade.log("<Handler> Connection refused. Game has already started.");
            clientHandler.sendMessage(PredefinedServerMessages.error("Game is already active. Disconnecting..."));
            clientHandler.closeResources();
            return;
        }

        Server server = clientHandler.getServer();

        synchronized (server) {
            boolean isAI = TypeCastingHelper.getBooleanFromMessage(clientHandler, message, "isAI");
            String messageProtocol = message.messageBody().get("protocol").toString();

            // Assert the group and the protocol version of the connecting client.
            if (!cfg.getProtocolVersion().equals(messageProtocol)) {
                ServerCommunicationFacade.log("<Handler> Connection refused. Protocol or Group is not supported.");
                clientHandler.sendMessage(PredefinedServerMessages.error("Protocol or Group is not supported. Disconnecting..."));
                clientHandler.closeResources();
                return;
            }

            // Add the client to the server.
            server.addClient(clientHandler);
            ServerCommunicationFacade.log("<Handler> Accepted client connection");

            // Retrieve the clientId, set the isAI flag and send a WELCOME message.
            ClientData retrievedClientData = server.getClients().get(clientHandler);
            retrievedClientData.setAI(isAI);

            // Send out the BackendClientData for all the previously connected clients.
            List<ClientData> validClientDataList = server.getClients().values().stream()
                    .filter(clientData -> null != clientData.getUsername() && -1 != clientData.getFigure())
                    .toList();

            validClientDataList.forEach(clientData ->
                    clientHandler.sendMessage(PredefinedServerMessages.playerAdded(
                            clientData.getClientId(),
                            clientData.getUsername(),
                            clientData.getFigure()
                    ))
            );

            // Send the name of the selected map to the client, if given.
            if (null != gameManager.getCourse()) {
                ServerCommunicationFacade.sendMessage(PredefinedServerMessages.mapSelected(
                        gameManager.getCourse().getName()), retrievedClientData.getClientId());
            }

            // Send out the welcome message to the client, so he can proceed.
            ServerCommunicationFacade.log("<Handler> Sending out id of: " + retrievedClientData.getClientId());
            clientHandler.sendMessage(PredefinedServerMessages.welcome(retrievedClientData.getClientId()));
        }
    }
}
