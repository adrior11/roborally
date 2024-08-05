package com.github.adrior.roborally.server.handlers;

import com.github.adrior.roborally.core.game.GameManager;
import com.github.adrior.roborally.core.game.TurnManager;
import com.github.adrior.roborally.core.map.RacingCourse;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.core.player.Robot;
import com.github.adrior.roborally.core.tile.PositionedTile;
import com.github.adrior.roborally.core.tile.tiles.StartPoint;
import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.handlers.IMessageHandler;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.*;
import com.github.adrior.roborally.server.util.AssertionHelper;
import com.github.adrior.roborally.server.util.TypeCastingHelper;
import com.github.adrior.roborally.utility.Vector;
import lombok.NonNull;

import java.util.List;

public class SetStartingPointHandler implements IMessageHandler<ClientHandler> {

    @Override
    public void handle(@NonNull Message message, @NonNull ClientHandler clientHandler) {
        if (!AssertionHelper.assertClientDataExists(clientHandler)) return;
        if (!AssertionHelper.assertGameIsActive(clientHandler)) return;

        Server server = clientHandler.getServer();
        int clientId = server.getClients().get(clientHandler).getClientId();

        if (!AssertionHelper.assertPlayerTurn(clientHandler, clientId)) return;

        GameManager gameManager = GameManager.getInstance();
        TurnManager turnManager = gameManager.getTurnManager();

        // Retrieve the position of the player robot to be set.
        int x = TypeCastingHelper.getIntFromMessage(clientHandler, message, "x");
        int y = TypeCastingHelper.getIntFromMessage(clientHandler, message, "y");

        // Assert if the starting point is valid.
        List<PositionedTile<StartPoint>> startPoints = RacingCourse.getPositionedTilesOfType(
                turnManager.getCurrentCourse().getTiles(), StartPoint.class);

        Vector selectedStartPoint = startPoints.stream()
                .filter(sp -> sp.position().equals(new Vector(x, y)))
                .findFirst()
                .map(PositionedTile::position)
                .orElse(null);

        if (null == selectedStartPoint) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                    "You've selected an invalid start point"), clientId);
            return;
        }

        // Assert if the selected starting point is already taken.
        if (getTakenStartingPositions().contains(selectedStartPoint)) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                    "Selected starting point is already taken"), clientId);
            return;
        }

        ServerCommunicationFacade.broadcast(PredefinedServerMessages.startingPointTaken(x, y, clientId));

        // Set the starting position via the TurnManager.
        turnManager.setStartingPoint(x, y, clientId);
    }


    /**
     * Retrieves a list of starting positions for all players who have set their starting point.
     *
     * @see Player
     * @see Robot
     * @see Vector
     *
     * @return a List of Vector objects representing the starting positions of the players who have set their starting point.
     */
    private List<Vector> getTakenStartingPositions() {
        GameManager gameManager = GameManager.getInstance();
        return gameManager.getPlayers().stream()
                .filter(player -> player.flags().isSetStartingPoint())
                .map(Player::robot)
                .map(Robot::getStartingPosition)
                .toList();
    }
}
