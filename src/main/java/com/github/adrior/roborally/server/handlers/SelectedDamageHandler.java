package com.github.adrior.roborally.server.handlers;

import com.github.adrior.roborally.core.card.CardType;
import com.github.adrior.roborally.core.card.SharedDeck;
import com.github.adrior.roborally.core.game.GameManager;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.handlers.IMessageHandler;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.*;
import com.github.adrior.roborally.server.util.AssertionHelper;
import com.github.adrior.roborally.server.util.TypeCastingHelper;
import lombok.NonNull;

import java.util.Arrays;
import java.util.Set;

import static com.github.adrior.roborally.core.card.CardType.*;

public class SelectedDamageHandler implements IMessageHandler<ClientHandler> {

    private static final Set<CardType> DAMAGE_CARDS = Set.of(SPAM, TROJAN, WORM, VIRUS);

    @Override
    public void handle(@NonNull Message message, @NonNull ClientHandler clientHandler) {
        if (!AssertionHelper.assertClientDataExists(clientHandler)) return;
        if (!AssertionHelper.assertGameIsActive(clientHandler)) return;

        Server server = clientHandler.getServer();
        int clientId = server.getClients().get(clientHandler).getClientId();
        GameManager gameManager = GameManager.getInstance();
        Player player = gameManager.getPlayerByID(clientId);

        // Assert if the backend is awaiting the damage card selection.
        if (!player.flags().isAwaitingDamageSelection()) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                    "You're not eligible to pick damage cards"), clientId);
            return;
        }

        String[] cardsString = TypeCastingHelper.getStringArrayFromMessage(clientHandler, message, "cards");

        for (String card : cardsString) {
            if (!AssertionHelper.assertValidCardType(clientHandler, card)) return;
            CardType cardType = CardType.fromString(card);

            // Assert if the selected card is a damage card.
            if (!DAMAGE_CARDS.contains(cardType)) {
                ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                        "You've provided an invalid card type"), clientId);
                return;
            }

            player.cardManager().addCardToDiscardPile(SharedDeck.drawDamageCard(cardType));
        }

        ServerCommunicationFacade.log(String.format("<Handler> Player %s chose his damage cards: %s",
                clientId, Arrays.toString(cardsString)));
        ServerCommunicationFacade.broadcast(PredefinedServerMessages.drawDamage(clientId, cardsString));

        player.flags().setAwaitingDamageSelection(false);
    }
}
