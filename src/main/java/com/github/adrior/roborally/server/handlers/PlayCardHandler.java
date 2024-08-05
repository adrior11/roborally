package com.github.adrior.roborally.server.handlers;

import com.github.adrior.roborally.core.card.Card;
import com.github.adrior.roborally.core.card.CardType;
import com.github.adrior.roborally.core.card.SharedDeck;
import com.github.adrior.roborally.core.card.cards.UpgradeCard;
import com.github.adrior.roborally.core.card.cards.UpgradeCard.UpgradeType;
import com.github.adrior.roborally.core.game.GameManager;
import com.github.adrior.roborally.core.game.GameState;
import com.github.adrior.roborally.core.game.TurnManager;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.exceptions.InvalidRegisterException;
import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.handlers.IMessageHandler;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.*;
import com.github.adrior.roborally.server.util.AssertionHelper;
import lombok.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.github.adrior.roborally.core.card.CardType.*;

public class PlayCardHandler implements IMessageHandler<ClientHandler> {

    private static final Set<CardType> UPGRADE_CARDS = Set.of(ADMIN_PRIVILEGE, REAR_LASER, MEMORY_SWAP, SPAM_BLOCKER);


    @Override
    public void handle(@NonNull Message message, @NonNull ClientHandler clientHandler) {
        if (!AssertionHelper.assertClientDataExists(clientHandler)) return;
        if (!AssertionHelper.assertGameIsActive(clientHandler)) return;

        Server server = clientHandler.getServer();
        GameManager gameManager = GameManager.getInstance();
        int clientId = server.getClients().get(clientHandler).getClientId();

        // Retrieve the card type given by the player.
        String card = message.messageBody().get("card").toString();
        if (!AssertionHelper.assertValidCardType(clientHandler, card)) return;
        CardType cardType = CardType.fromString(card);

        // Assert if the backend is awaiting user input regarding an upgrade card.
        Player player = gameManager.getPlayerByID(clientId);
        if (player.flags().isAwaitingUpgradeCard()) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                    "You first have to complete the play of an upgrade card"), clientId);
            return;
        }

        // Assert if the backend is awaiting the damage card selection.
        if (player.flags().isAwaitingDamageSelection()) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                    "You first have to complete the selection of your damage cards"), clientId);
            return;
        }

        ServerCommunicationFacade.log(String.format(
                "<Handler> Handling %s card for Player %s", cardType, clientId));

        // Assert if the selected card is an upgrade or programming card.
        if (UPGRADE_CARDS.contains(cardType)) {
            handleUpgradeCard(clientHandler, player, gameManager, clientId, card, cardType);
        } else {
            handleProgrammingCard(clientHandler, player, gameManager, clientId, card, cardType);
        }
    }


    /**
     * Handles the logic for executing an upgrade card.
     *
     * @param clientHandler the client handler
     * @param player        the player executing the card
     * @param gameManager   the game manager instance
     * @param clientId      the ID of the client
     * @param card          the card being played
     * @param cardType      the type of card being played
     */
    private static void handleUpgradeCard(@NonNull ClientHandler clientHandler, @NonNull Player player,
                                          @NonNull GameManager gameManager, int clientId, @NonNull String card,
                                          @NonNull CardType cardType) {
        TurnManager turnManager = gameManager.getTurnManager();

        if (CardType.REAR_LASER == cardType || ADMIN_PRIVILEGE == cardType) {
            return; // RearLaser is a passive ability & AdminPrivilege has a distinct message type.
        }

        // Assert if it's the programming phase.
        if (!AssertionHelper.assertValidPhase(clientHandler, GameState.PROGRAMMING_PHASE)) return;

        // Assert if the player has the upgrade card installed.
        UpgradeCard installedUpgrade = (UpgradeCard) player.installedUpgrades().getCardByType(cardType);
        if (null == installedUpgrade) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(String.format(
                    "You haven't installed the %s upgrade card", card)), clientId);
            return;
        }

        // Inform other players about the card execution & execute the effect.
        ServerCommunicationFacade.log(String.format(
                "<Handler> Player %s activated an upgrade card: %s", clientId, installedUpgrade.getCardType().toString()));
        ServerCommunicationFacade.broadcast(PredefinedServerMessages.cardPlayed(clientId, cardType.toString()));

        installedUpgrade.execute(turnManager, player);

        // Remove the installed upgrade card if it's a temporary upgrade.
        if (UpgradeType.TEMPORARY == installedUpgrade.getUpgradeType()) {
            ServerCommunicationFacade.log(String.format(
                    "<Handler> Uninstalling the temporary upgrade %s for player %s", installedUpgrade.getCardType().toString(), clientId));
            SharedDeck.upgradeDeck.addCard(player.installedUpgrades().retrieveCardByType(installedUpgrade.getCardType()));
        }
    }


    /**
     * Handles the logic for executing a programming card.
     *
     * @param clientHandler the client handler
     * @param player        the player executing the card
     * @param gameManager   the game manager instance
     * @param clientId      the ID of the client
     * @param card          the card being played
     * @param cardType      the type of card being played
     */
    private static void handleProgrammingCard(@NonNull ClientHandler clientHandler, @NonNull Player player,
                                              @NonNull GameManager gameManager, int clientId, @NonNull String card,
                                              @NonNull CardType cardType) {
        TurnManager turnManager = gameManager.getTurnManager();

        // Assert if it's the activation phase.
        if (!AssertionHelper.assertValidPhase(clientHandler, GameState.ACTIVATION_PHASE)) return;

        // Assert it is the players turn.
        if (!AssertionHelper.assertPlayerTurn(clientHandler, clientId)) return;

        // Assert if the register contains a card.
        Card cardInRegister = player.programmingRegister().getRegister(turnManager.getCurrentRegisterIndex());
        if (null == cardInRegister) {
            ServerCommunicationFacade.log("<Handler> Error, Register not found");
            throw new InvalidRegisterException("Register can't be null on activation");
        }

        // Assert if the player has already played a card.
        if (player.flags().isPlayedRegister()) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error("You've already played the card for your current register"), clientId);
            return;
        }

        // Assert that the card equals the in from the current register.
        if (cardInRegister.getCardType() != cardType) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error("Played card is not the one from your current register"), clientId);
            return;
        }

        // Logging the complete register for debugging the InvalidGameStateException for the Spam/Again Card
        List<String> cardsInRegisterString = player.programmingRegister().getAllRegisters().stream()
                .map(Card::getCardType)
                .map(CardType::toString)
                .toList();
        ServerCommunicationFacade.log(String.format(
                "<Handler> Player %s's register: %s", player.clientId(), Arrays.toString(cardsInRegisterString.toArray())
        ));

        // Inform other players about the card execution & execute the effect.
        ServerCommunicationFacade.log(String.format("<Handler> Player %s played card: %s", clientId, card));
        ServerCommunicationFacade.broadcast(PredefinedServerMessages.cardPlayed(clientId, cardType.toString()));
        cardInRegister.execute(turnManager, player);

        // Set player-hasPlayedRegister flag to true and advance to the next player.
        player.flags().setPlayedRegister(true);

        if (GameState.ACTIVATION_PHASE == turnManager.getCurrentPhase()) turnManager.advancePlayCard();
    }
}