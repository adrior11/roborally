package com.github.adrior.roborally.commands.executors;

import com.github.adrior.roborally.commands.ICommand;
import com.github.adrior.roborally.core.game.GameManager;
import com.github.adrior.roborally.core.game.MovementExecutor;
import com.github.adrior.roborally.core.map.RacingCourse;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.ServerCommunicationFacade;

import java.util.List;

/**
 * Command to reboot a player's robot.
 */
public class RebootCommand implements ICommand {

    private static final GameManager gameManager = GameManager.getInstance();

    @Override
    public void execute(int id, String[] args) {
        if (1 < args.length) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                    "Usage: /reboot [clientId]"), id);
            return;
        }

        Player player = gameManager.getPlayerByID(0 == args.length ? id : Integer.parseInt(args[0]));

        if (null == player) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                    "Couldn't retrieve the player to reshuffle his discard pile"), id);
            return;
        }

        RacingCourse racingCourse = gameManager.getTurnManager().getCurrentCourse();
        List<Player> players = gameManager.getTurnManager().getPlayers();
        MovementExecutor.reboot(racingCourse, player, player.robot().getPosition(), players);
    }
}
