package com.github.adrior.roborally.server.handlers;

import com.github.adrior.roborally.core.card.Card;
import com.github.adrior.roborally.core.card.CardType;
import com.github.adrior.roborally.core.game.GameManager;
import com.github.adrior.roborally.core.game.GameState;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.handlers.IMessageHandler;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.*;
import com.github.adrior.roborally.server.util.AssertionHelper;
import com.github.adrior.roborally.server.util.TypeCastingHelper;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DiscardSomeHandler implements IMessageHandler<ClientHandler> {

    @Override
    public void handle(@NonNull Message message, @NonNull ClientHandler clientHandler) {
        if (!AssertionHelper.assertClientDataExists(clientHandler)) return;
        if (!AssertionHelper.assertGameIsActive(clientHandler)) return;

        Server server = clientHandler.getServer();
        int clientId = server.getClients().get(clientHandler).getClientId();
        Player player = GameManager.getInstance().getPlayerByID(clientId);

        if (!AssertionHelper.assertValidPhase(clientHandler, GameState.PROGRAMMING_PHASE)) {
            player.flags().setAwaitingUpgradeCard(false);
            return;
        }

        // Assert if the backend is awaiting the damage card selection.
        if (player.flags().isAwaitingDamageSelection()) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                    "You first have to complete the selection of your damage cards"), clientId);
            return;
        }

        // Assert if the player has played a memory swap upgrade card.
        if (!player.flags().isAwaitingUpgradeCard()) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                    "You haven't activated a memory swap upgrade card"), clientId);
            return;
        }

        // Assert if the selected cards are valid.
        String[] cards = TypeCastingHelper.getStringArrayFromMessage(clientHandler, message, "cards");
        List<CardType> cardTypes = new ArrayList<>();

        for (String card : cards) {
            if (!AssertionHelper.assertValidCardType(clientHandler, card)) return;
            cardTypes.add(CardType.fromString(card));
        }

        // Assert if the player has the selected cards in their hand.
        for (CardType cardType : cardTypes) {
            if (!player.cardManager().hasCardTypeInHand(cardType)) {
                ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(String.format(
                        "You don't have %s in your hand, please try again", cardType.toString())), clientId);
                break;
            }
        }

        ServerCommunicationFacade.log(String.format(
                "<Handler> Player %s exchanged following cards for the memory swap: %s", clientId, Arrays.toString(cards)));

        // Return the selected cards to the draw deck.
        for (CardType cardType : cardTypes) {
            Card card = player.cardManager().retrieveCardFromHandByType(cardType);
            player.cardManager().getDrawDeck().addCardToTop(card);
        }

        player.flags().setAwaitingUpgradeCard(false);

        String[] newHand = player.cardManager().getHand().getAllCardNames();
        ServerCommunicationFacade.log(String.format(
                "<Handler> Player %s's new hand after the memory swap: %s", clientId, Arrays.toString(newHand)));
        ServerCommunicationFacade.sendMessage(PredefinedServerMessages.yourCards(newHand), clientId);
    }
}

