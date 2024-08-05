package com.github.adrior.roborally.server.handlers;

import com.github.adrior.roborally.core.card.Card;
import com.github.adrior.roborally.core.card.CardType;
import com.github.adrior.roborally.core.game.GameManager;
import com.github.adrior.roborally.core.game.GameState;
import com.github.adrior.roborally.core.game.util.Timer;
import com.github.adrior.roborally.core.game.TurnManager;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.handlers.IMessageHandler;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.*;
import com.github.adrior.roborally.server.util.AssertionHelper;
import com.github.adrior.roborally.server.util.TypeCastingHelper;
import lombok.NonNull;

import java.util.Arrays;
import java.util.List;

public class SelectedCardHandler implements IMessageHandler<ClientHandler> {

    @Override
    public void handle(@NonNull Message message, @NonNull ClientHandler clientHandler) {
        if (!AssertionHelper.assertClientDataExists(clientHandler)) return;
        if (!AssertionHelper.assertGameIsActive(clientHandler)) return;
        if (!AssertionHelper.assertValidPhase(clientHandler, GameState.PROGRAMMING_PHASE)) return;

        Server server = clientHandler.getServer();
        int clientId = server.getClients().get(clientHandler).getClientId();
        GameManager gameManager = GameManager.getInstance();

        // Assert if the backend is awaiting user input regarding an upgrade card.
        Player player = gameManager.getPlayerByID(clientId);
        if (player.flags().isAwaitingUpgradeCard()) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                    "You first have to complete the play of an upgrade card."), clientId);
            return;
        }

        // Assert if the backend is awaiting the damage card selection.
        if (player.flags().isAwaitingDamageSelection()) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                    "You first have to complete the selection of your damage cards"), clientId);
            return;
        }

        // Assert the correct input for the register.
        int register = TypeCastingHelper.getIntFromMessage(clientHandler, message, "register") - 1;

        if (0 > register || 4 < register) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(String.format(
                    "You've provided an invalid register of %s to play your card in.", register)), clientId);
            return;
        }

        String card = message.messageBody().get("card").toString();
        handleCardPlacement(clientHandler, clientId, player, register, card);
    }


    /**
     * Handles the card placement in the specified register.
     *
     * @param clientHandler the clientHandler of the client
     * @param clientId      the ID of the client
     * @param player        the player placing the card
     * @param register      the register in which to place the card
     * @param card          the card to be placed
     */
    private void handleCardPlacement(ClientHandler clientHandler, int clientId, Player player, int register, String card) {
        TurnManager turnManager = GameManager.getInstance().getTurnManager();

        // Assert that the player doesn't play an again card in the first register.
        if (0 == register && card.equals("Again")) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error(
                    "You cannot place an Again card in the first register."), clientId);
            return;
        }

        synchronized (turnManager) {
            // Removes a Card from the given register if the card is Null.
            Card removedCard = player.programmingRegister().removeCardFromRegister(register);

            if (null != removedCard) {
                if (card.equals("Null")) ServerCommunicationFacade.broadcast(
                        PredefinedServerMessages.cardSelected(clientId, register + 1, false));

                // Place the removed card back to the player hand.
                player.cardManager().addCardToHand(removedCard);
                turnManager.assertTotalCardCount();
            }
        }

        if (card.equals("Null")) return;

        CardType cardType = CardType.fromString(card);
        if (!AssertionHelper.assertValidCardType(clientHandler, card)) return;
        if (!isCardAlreadyPlaced(clientId, player, register, cardType)) return;
        if (!isCardInPlayerHand(clientId, player, cardType)) return;

        placeCardInRegister(clientId, player, register, cardType);
    }


    /**
     * Checks if the card is already placed in the specified register.
     *
     * @param clientId the ID of the client
     * @param player   the player placing the card
     * @param register the register to check
     * @param cardType the type of the card
     * @return true if the card is not already placed, false otherwise
     */
    private boolean isCardAlreadyPlaced(int clientId, Player player, int register, CardType cardType) {
        Card cardInRegister = player.programmingRegister().getRegister(register);
        if (null != cardInRegister && cardType == cardInRegister.getCardType()) {
            ServerCommunicationFacade.log(String.format("<Handler> Player %s tried to place card %s into register %s, which has already been placed.", clientId, cardType, register));
            return false;
        }
        return true;
    }


    /**
     * Checks if the card is in the player's hand.
     *
     * @param clientId the ID of the client
     * @param player   the player placing the card
     * @param cardType the type of the card
     * @return true if the card is in the player's hand, false otherwise
     */
    private boolean isCardInPlayerHand(int clientId, Player player, CardType cardType) {
        if (!player.cardManager().hasCardTypeInHand(cardType)) {
            ServerCommunicationFacade.sendMessage(PredefinedServerMessages.error("You've tried to play a card which is not in your hand."), clientId);
            return false;
        }
        return true;
    }


    /**
     * Places the card in the specified register.
     *
     * @param clientId the ID of the client
     * @param player   the player placing the card
     * @param register the register in which to place the card
     * @param cardType the type of the card
     */
    private void placeCardInRegister(int clientId, Player player, int register, CardType cardType) {
        TurnManager turnManager = GameManager.getInstance().getTurnManager();

        synchronized (turnManager) {
            Card cardFromHand = player.cardManager().retrieveCardFromHandByType(cardType);
            player.programmingRegister().setRegister(register, cardFromHand);
            ServerCommunicationFacade.broadcast(PredefinedServerMessages.cardSelected(clientId, register+1, true));

            logPlayerRegisters(player);

            turnManager.assertTotalCardCount();

            // Check if all register slots are filled with a card, thus starting the timer if it is not already running.
            if (player.programmingRegister().isFilled()) {
                player.flags().setSelectionFinished(true);
                player.cardManager().discardHand();
                ServerCommunicationFacade.broadcast(PredefinedServerMessages.selectionFinished(clientId));

                if (GameState.PROGRAMMING_PHASE == turnManager.getCurrentPhase()
                        && !Timer.getInstance(turnManager).getIsRunning().get()
                        && !turnManager.allPlayersDidFillRegisters()) {
                    turnManager.startTimer();
                } else if (turnManager.allPlayersDidFillRegisters() && Timer.getInstance(turnManager).getIsRunning().get()) {
                    turnManager.cancelTimer();
                }
            }

            turnManager.assertTotalCardCount();
        }
    }


    /**
     * Logs the player's current registers.
     *
     * @param player the player whose registers are being logged
     */
    private void logPlayerRegisters(Player player) {
        List<String> registerCardStrings = Arrays.stream(player.programmingRegister().getRegisters())
                .map(reg -> null != reg ? reg.getCardType().toString() : "empty")
                .toList();
        ServerCommunicationFacade.log(String.format("<Handler> Player %s current registers: %s", player.clientId(), Arrays.toString(registerCardStrings.toArray())));
    }
}