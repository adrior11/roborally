package com.github.adrior.roborally.server.handlers;

import com.github.adrior.roborally.core.card.CardType;
import com.github.adrior.roborally.core.game.GameManager;
import com.github.adrior.roborally.core.game.GameState;
import com.github.adrior.roborally.core.game.TurnManager;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.handlers.IMessageHandler;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.*;
import com.github.adrior.roborally.server.util.AssertionHelper;
import com.github.adrior.roborally.server.util.TypeCastingHelper;
import com.github.adrior.roborally.utility.Pair;
import lombok.NonNull;

public class ChooseRegisterHandler implements IMessageHandler<ClientHandler> {

    @Override
    public void handle(@NonNull Message message, @NonNull ClientHandler clientHandler) {
        if (!AssertionHelper.assertClientDataExists(clientHandler)) return;
        if (!AssertionHelper.assertGameIsActive(clientHandler)) return;
        if (!AssertionHelper.assertValidPhase(clientHandler, GameState.PROGRAMMING_PHASE)) return;

        Server server = clientHandler.getServer();
        int clientId = server.getClients().get(clientHandler).getClientId();
        GameManager gameManager = GameManager.getInstance();
        TurnManager turnManager = gameManager.getTurnManager();
        Player player = gameManager.getPlayerByID(clientId);

        // Assert if the backend is awaiting the damage card selection.
        if (player.flags().isAwaitingDamageSelection()) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                    "You first have to complete the selection of your damage cards"), clientId);
            return;
        }

        // Assert if the player owns an admin privilege card.
        if (null == player.installedUpgrades().getCardByType(CardType.ADMIN_PRIVILEGE)) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                    "You don't have an admin privilege card installed"), clientId);
            return;
        }

        // Assert if the player has already played an admin privilege card for this round.
        if (turnManager.hasPlayerRegisteredAdminPriority(clientId)) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                    "You've already activated your admin privilege for this round"), clientId);
            return;
        }

        int register = TypeCastingHelper.getIntFromMessage(clientHandler, message, "register");

        if (0 > register || 4 < register) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(String.format(
                    "You've provided an invalid register of %s to play your card in", register)), clientId);
            return;
        }

        turnManager.getAdminPriorityQueue().addLast(new Pair<>(register, clientId));
        ServerCommunicationFacade.log(String.format(
                "<Handler> Player %s activated an admin priority for register: %s", clientId, register));
        ServerCommunicationFacade.broadcast(PredefinedServerMessages.registerChosen(clientId, register));
    }
}
