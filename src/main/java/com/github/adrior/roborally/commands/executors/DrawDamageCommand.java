package com.github.adrior.roborally.commands.executors;

import com.github.adrior.roborally.commands.ICommand;
import com.github.adrior.roborally.core.card.Card;
import com.github.adrior.roborally.core.card.CardType;
import com.github.adrior.roborally.core.card.SharedDeck;
import com.github.adrior.roborally.core.game.GameManager;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.exceptions.InvalidGameStateException;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.ServerCommunicationFacade;
import lombok.NonNull;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.github.adrior.roborally.core.card.CardType.*;

/**
 * Command to draw a damage card for a player's robot.
 */
public class DrawDamageCommand implements ICommand {
    private static final Set<CardType> DAMAGE_CARDS = Set.of(SPAM, TROJAN, WORM, VIRUS);

    private static final GameManager gameManager = GameManager.getInstance();

    @Override
    public void execute(int id, @NonNull String[] args) {
        if (2 < args.length || 1 > args.length) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                    "Usage: /drawdamage [clientId] (spam | trojan | worm | virus)"), id);
            return;
        }

        Player player = gameManager.getPlayerByID(1 == args.length ? id : Integer.parseInt(args[0]));

        if (null == player) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                    "Couldn't retrieve the player to rotate"), id);
            return;
        }

        if (player.flags().isAwaitingDamageSelection()) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                    "You first have to complete the selection of your damage cards"), id);
            return;
        }

        CardType cardType = CardType.valueOf(args[1 == args.length ? 0 : 1].toUpperCase(Locale.ROOT));

        if (!DAMAGE_CARDS.contains(cardType)) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                    "You haven't selected an valid damage card"), id);
            return;
        }

        List<Card> damageCard = SharedDeck.drawCards(cardType, 1);

        if (!damageCard.isEmpty()) {
            damageCard.forEach(card -> player.cardManager().addCardToDiscardPile(card));

            ServerCommunicationFacade.broadcast(PredefinedServerMessages.drawDamage(player.clientId(), new String[]{cardType.toString()}));
        } else {
            String[] availablePiles;
            try {
                availablePiles = SharedDeck.assertSharedDeckSizes(1);
            } catch (InvalidGameStateException e) {
                ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(e.getMessage()), player.clientId());
                return;
            }

            player.flags().setAwaitingDamageSelection(true);
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.pickDamage(1, availablePiles), player.clientId());
        }
    }
}
