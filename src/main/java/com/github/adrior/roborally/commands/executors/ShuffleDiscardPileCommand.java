package com.github.adrior.roborally.commands.executors;

import com.github.adrior.roborally.commands.ICommand;
import com.github.adrior.roborally.core.game.GameManager;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.ServerCommunicationFacade;
import lombok.NonNull;

/**
 * Command to shuffle a player's discard pile into their deck.
 */
public class ShuffleDiscardPileCommand implements ICommand {

    private static final GameManager gameManager = GameManager.getInstance();

    @Override
    public void execute(int id, @NonNull String[] args) {
        if (1 < args.length) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                    "Usage: /shufflediscard [clientId]"), id);
            return;
        }

        Player player = gameManager.getPlayerByID(0 == args.length ? id : Integer.parseInt(args[0]));

        if (null == player) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                    "Couldn't retrieve the player to reshuffle his discard pile"), id);
            return;
        }

        player.cardManager().reshuffleDiscardPileIntoDeck();
        ServerCommunicationFacade.broadcast(PredefinedServerMessages.shuffleCoding(player.clientId()));
    }
}
