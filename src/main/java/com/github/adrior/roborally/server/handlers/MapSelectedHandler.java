package com.github.adrior.roborally.server.handlers;

import com.github.adrior.roborally.core.game.GameManager;
import com.github.adrior.roborally.core.map.AvailableCourses;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.core.tile.Tile;
import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.handlers.IMessageHandler;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.*;
import com.github.adrior.roborally.server.util.AssertionHelper;
import lombok.NonNull;

import java.util.List;

public class MapSelectedHandler implements IMessageHandler<ClientHandler> {

    @Override
    public void handle(@NonNull Message message, @NonNull ClientHandler clientHandler) {
        if (!AssertionHelper.assertClientDataExists(clientHandler)) return;
        if (!AssertionHelper.assertGameIsNotActive(clientHandler)) return;

        Server server = clientHandler.getServer();
        int clientId = server.getClients().get(clientHandler).getClientId();
        GameManager gameManager = GameManager.getInstance();
        Player player = gameManager.getPlayerByID(clientId);

        // Assert if the player can select the map.
        if (!player.flags().isSelectingMap()) {
            clientHandler.sendMessage(PredefinedServerMessages.error("You don't have the rights to select a map"));
            return;
        }

        // Create the RacingCourse based on the received map by the client.
        try {
            String selectedMap = message.messageBody().get("map").toString();
            AvailableCourses selectedRacingCourse = AvailableCourses.fromString(selectedMap);

            ServerCommunicationFacade.log(String.format("<Handler> Player %s has selected the RacingCourse: %s",
                    clientId, selectedRacingCourse));

            gameManager.initializeCourse(selectedRacingCourse);

            ServerCommunicationFacade.broadcast(PredefinedServerMessages.mapSelected(selectedRacingCourse.toString()));
        } catch (IllegalArgumentException e) {
            clientHandler.sendMessage(PredefinedServerMessages.error("Selected Racing Course is not available"));
            return;
        }

        // Check if all players are ready after the Map has been selected, to start the game.
        if (gameManager.getPlayers().size() == server.countValidClients()
                && server.getClients().size() >= gameManager.getMinPlayers()) {
            List<List<List<Tile>>> mapArray = gameManager.getCourse().getTiles();
            ServerCommunicationFacade.broadcast(PredefinedServerMessages.gameStarted(mapArray));
            gameManager.startGame();
        }
    }
}