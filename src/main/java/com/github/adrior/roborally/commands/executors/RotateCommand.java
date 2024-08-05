package com.github.adrior.roborally.commands.executors;

import com.github.adrior.roborally.commands.ICommand;
import com.github.adrior.roborally.core.game.GameManager;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.ServerCommunicationFacade;
import lombok.NonNull;

/**
 * Command to rotate a player's robot.
 */
public class RotateCommand implements ICommand {

    private static final GameManager gameManager = GameManager.getInstance();

    @Override
    public void execute(int id, @NonNull String[] args) {
        if (1 > args.length || 2 < args.length) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                    "Usage: /rotate [clientId] (clockwise | counterclockwise)"), id);
            return;
        }

        Player player = gameManager.getPlayerByID(1 == args.length ? id : Integer.parseInt(args[0]));

        if (null == player) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                    "Couldn't retrieve the player to rotate"), id);
            return;
        }

        switch (args[2 == args.length ? 1 : 0]) {
            case "clockwise":
                player.robot().setOrientation(player.robot().getOrientation().turnRight());
                ServerCommunicationFacade.broadcast(PredefinedServerMessages.playerTurning(player.clientId(), "clockwise"));
                break;
            case "counterclockwise":
                player.robot().setOrientation(player.robot().getOrientation().turnLeft());
                ServerCommunicationFacade.broadcast(PredefinedServerMessages.playerTurning(player.clientId(), "counterclockwise"));
                break;
            default:
                ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                        "Usage: /rotate [clientId] (clockwise | counterclockwise)"), id);
        }
    }
}
