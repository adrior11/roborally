package com.github.adrior.roborally.commands.executors;

import com.github.adrior.roborally.commands.ICommand;
import com.github.adrior.roborally.core.game.GameManager;
import com.github.adrior.roborally.core.map.RacingCourse;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.core.tile.Tile;
import com.github.adrior.roborally.core.tile.tiles.NullTile;
import com.github.adrior.roborally.core.tile.tiles.Pit;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.ServerCommunicationFacade;
import com.github.adrior.roborally.utility.Vector;
import lombok.NonNull;

import java.util.List;

/**
 * Command to teleport a player's robot to a specified position on the map.
 */
public class TeleportCommand implements ICommand {

    private static final GameManager gameManager = GameManager.getInstance();

    @Override
    public void execute(int id, @NonNull String[] args) {
        if (2 > args.length || 3 < args.length) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                    "Usage: /teleport [clientId] <x> <y>"), id);
            return;
        }

        RacingCourse racingCourse = gameManager.getTurnManager().getCurrentCourse();
        Player player = gameManager.getPlayerByID(id);

        try {
            if (3 == args.length) player = gameManager.getPlayerByID(Integer.parseInt(args[0]));

            if (null == player) {
                ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                        "Couldn't retrieve the player to reshuffle his discard pile"), id);
                return;
            }

            int x = Integer.parseInt(args[3 == args.length ? 1 : 0]);
            int y = Integer.parseInt(args[3 == args.length ? 2 : 1]);

            List<Tile> tiles = racingCourse.getTileAt(new Vector(x, y));
            boolean cannotTeleport = tiles.stream().anyMatch(tile -> tile instanceof NullTile || tile instanceof Pit);

            if (tiles.isEmpty() || cannotTeleport) {
                ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                        "Cannot invoke 'teleport' command as the position is out of map or a pit"), id);
                return;
            }

            player.robot().setPosition(new Vector(x, y));
            ServerCommunicationFacade.broadcast(PredefinedServerMessages.movement(player.clientId(), x, y));

        } catch (NumberFormatException e) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                    "Invalid clientId, x, or y. They should be integer numbers."), id);
        }
    }
}
