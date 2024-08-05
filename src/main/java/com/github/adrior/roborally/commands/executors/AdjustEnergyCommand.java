package com.github.adrior.roborally.commands.executors;

import com.github.adrior.roborally.commands.ICommand;
import com.github.adrior.roborally.core.game.GameManager;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.ServerCommunicationFacade;
import lombok.NonNull;

/**
 * Command to adjust the energy of a player's robot.
 */
public class AdjustEnergyCommand implements ICommand {

    private static final GameManager gameManager = GameManager.getInstance();

    @Override
    public void execute(int id, @NonNull String[] args) {
        if (2 < args.length || 1 > args.length) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                    "Usage: /adjustenergy [clientId] <amount>"), id);
            return;
        }

        Player player = gameManager.getPlayerByID(1 == args.length ? id : Integer.parseInt(args[0]));
        int amount = Integer.parseInt(1 == args.length ? args[0] : args[1]);

        if (null == player) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                    "Couldn't retrieve the player to reshuffle his discard pile"), id);
            return;
        }

        player.robot().adjustEnergy(amount);
        ServerCommunicationFacade.broadcast(PredefinedServerMessages.energy(player.clientId(), player.robot().getEnergy(), "Cheat"));
    }
}
