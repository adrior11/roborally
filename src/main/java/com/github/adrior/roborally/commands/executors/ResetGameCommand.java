package com.github.adrior.roborally.commands.executors;

import com.github.adrior.roborally.commands.ICommand;
import com.github.adrior.roborally.core.game.GameManager;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.ServerCommunicationFacade;

/**
 * Command to reset the game.
 */
public class ResetGameCommand implements ICommand {

    private static final GameManager gameManager = GameManager.getInstance();

    @Override
    public void execute(int id, String[] args) {
        if (0 != args.length) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                    "Usage: /resetgame"), id);
            return;
        }

        gameManager.endGame(id);
    }
}
