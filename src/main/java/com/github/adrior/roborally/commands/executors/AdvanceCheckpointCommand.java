package com.github.adrior.roborally.commands.executors;

import com.github.adrior.roborally.commands.ICommand;
import com.github.adrior.roborally.core.game.GameManager;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.ServerCommunicationFacade;

/**
 * Command to advance the checkpoint of a player's robot.
 */
public class AdvanceCheckpointCommand implements ICommand {

    private static final GameManager gameManager = GameManager.getInstance();

    @Override
    public void execute(int id, String[] args) {
        if (1 < args.length) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                    "Usage: /advancecheckpoint [clientId]"), id);
            return;
        }

        Player player = gameManager.getPlayerByID(0 == args.length ? id : Integer.parseInt(args[0]));

        if (null == player) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                    "Couldn't retrieve the player to reshuffle his discard pile"), id);
            return;
        }

        player.robot().incrementCheckpoint();
        ServerCommunicationFacade.broadcast(PredefinedServerMessages.checkpointReached(player.clientId(), player.robot().getCheckpoint()));
        gameManager.getTurnManager().checkWinConditions();
    }
}
