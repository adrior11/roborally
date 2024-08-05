package com.github.adrior.roborally.server.handlers;

import com.github.adrior.roborally.core.game.GameManager;
import com.github.adrior.roborally.core.map.AvailableCourses;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.handlers.IMessageHandler;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.*;
import com.github.adrior.roborally.server.util.AssertionHelper;
import com.github.adrior.roborally.server.util.TypeCastingHelper;
import lombok.NonNull;

public class SetStatusHandler implements IMessageHandler<ClientHandler> {

    @Override
    public void handle(@NonNull Message message, @NonNull ClientHandler clientHandler) {
        if (!AssertionHelper.assertClientDataExists(clientHandler)) return;
        if (!AssertionHelper.assertGameIsNotActive(clientHandler)) return;

        Server server = clientHandler.getServer();
        ClientData retrievedClientData = server.getClients().get(clientHandler);

        boolean isAI = retrievedClientData.isAI();
        int clientId = retrievedClientData.getClientId();

        boolean isReady = TypeCastingHelper.getBooleanFromMessage(clientHandler, message, "ready");
        ServerCommunicationFacade.log(String.format("<Handler> Player %s set his status to '%s'",
                clientId, isReady ? "ready" : "not ready"));

        // Create a player instance for the client who's ready and set the isAI flag.
        GameManager gameManager = GameManager.getInstance();

        if (null == gameManager.getPlayerByID(clientId) && isReady) {
            gameManager.addPlayer(clientId);
            gameManager.getPlayerByID(clientId).flags().setAI(isAI);
            ServerCommunicationFacade.broadcast(PredefinedServerMessages.playerStatus(clientId, true));
        }

        // Retrieve the first connected non AI player.
        Player firstConnectPlayer = gameManager.getFirstConnectedNonAIPlayer();

        // Allow the first connected non AI player to choose the map.
        if (isReady && null != firstConnectPlayer && clientId == firstConnectPlayer.clientId()) {
            firstConnectPlayer.flags().setSelectingMap(true);
            clientHandler.sendMessage(PredefinedServerMessages.selectMap(AvailableCourses.getFormattedNames()));
        }

        // Remove the associated player if the isReady flag gets revoked.
        if (!isReady) {
            Player player = gameManager.getPlayerByID(clientId);
            gameManager.getPlayers().remove(player);

            // If the player was currently choosing a map, allow the next connected player to choose if there is any.
            if (!gameManager.isSomeoneSelectingMap() && !gameManager.getPlayers().isEmpty()
                    && null != gameManager.getFirstConnectedNonAIPlayer()) {
                firstConnectPlayer = gameManager.getFirstConnectedNonAIPlayer();
                firstConnectPlayer.flags().setSelectingMap(true);

                ServerCommunicationFacade.sendMessage(PredefinedServerMessages.selectMap(
                        AvailableCourses.getFormattedNames()),
                        firstConnectPlayer.clientId());
            }
        }

        // Check if all the clients are ready (player instance created), the minimum amount has been reached & the Map has been selected.
        if (gameManager.getPlayers().size() == server.countValidClients()
                && server.getClients().size() >= gameManager.getMinPlayers()
                && null != gameManager.getCourse()) {
            startGame();
        } else if (gameManager.getPlayers().size() == server.getClients().size()
                && server.getClients().size() == gameManager.getMinPlayers()
                && null == gameManager.getFirstConnectedNonAIPlayer()) {
            ServerCommunicationFacade.log("<Handler> Selecting a random map for the full bot lobby...");
            gameManager.initializeCourse(AvailableCourses.getRandomCourse());
            startGame();
        }
    }

    /**
     * Notifies all players that the {@link GameManager} has started a game using the {@link ServerCommunicationFacade}.
     */
    private static void startGame() {
        GameManager gameManager = GameManager.getInstance();
        ServerCommunicationFacade.broadcast(PredefinedServerMessages.gameStarted(gameManager.getCourse().getTiles()));
        gameManager.startGame();
    }
}