package com.github.adrior.roborally.commands.executors;

import com.github.adrior.roborally.commands.ICommand;
import com.github.adrior.roborally.core.game.GameManager;
import com.github.adrior.roborally.core.game.MovementExecutor;
import com.github.adrior.roborally.core.map.RacingCourse;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.ServerCommunicationFacade;
import com.github.adrior.roborally.utility.Orientation;

import java.util.List;
import java.util.Locale;

/**
 * Command to move a player's robot in a specified direction.
 */
public class MoveCommand implements ICommand {

    private static final GameManager gameManager = GameManager.getInstance();

    @Override
    public void execute(int id, String[] args) {
        if (2 < args.length) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                    "Usage: /move [clientId] [top | bottom | right | left]"), id);
            return;
        }

        RacingCourse racingCourse = gameManager.getTurnManager().getCurrentCourse();
        Player player = gameManager.getPlayerByID(id);
        List<Player> players = gameManager.getTurnManager().getPlayers();

        if (0 == args.length) {
            Orientation orientation = player.robot().getOrientation();
            MovementExecutor.moveRobot(racingCourse, player, players, orientation);
        }

        if (1 == args.length) {
            int targetId = -1;
            Orientation orientation = null;

            try {
                targetId = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                orientation = Orientation.valueOf(args[0].toUpperCase(Locale.ROOT));
            }

            if (-1 != targetId) {
                player = gameManager.getPlayerByID(targetId);
                orientation = player.robot().getOrientation();
                MovementExecutor.moveRobot(racingCourse, player, players, orientation);
            } else if (null != orientation) {
                MovementExecutor.moveRobot(racingCourse, player, players, orientation);
            }
        }

        if (2 == args.length) {
            int targetId = Integer.parseInt(args[0]);

            player = gameManager.getPlayerByID(targetId);
            Orientation orientation = player.robot().getOrientation();
            MovementExecutor.moveRobot(racingCourse, player, players, orientation);
        }
    }
}
