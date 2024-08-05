package com.github.adrior.roborally.server.util;

import com.github.adrior.roborally.core.card.CardType;
import com.github.adrior.roborally.core.game.GameManager;
import com.github.adrior.roborally.core.game.GameState;
import com.github.adrior.roborally.core.game.TurnManager;
import com.github.adrior.roborally.core.player.Player;
import com.github.adrior.roborally.message.utils.PredefinedServerMessages;
import com.github.adrior.roborally.server.ClientData;
import com.github.adrior.roborally.server.ClientHandler;
import com.github.adrior.roborally.server.Server;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

/**
 * Utility class for common assertion methods used by various message handlers.
 */
@UtilityClass
public class AssertionHelper {

    /**
     * Asserts that the {@link ClientData} exists on the {@link Server}.
     *
     * @param clientHandler the client handler
     * @return true if client data exists, false otherwise
     */
    public static boolean assertClientDataExists(@NonNull ClientHandler clientHandler) {
        Server server = clientHandler.getServer();
        ClientData clientData = server.getClients().get(clientHandler);

        if (null == clientData) {
            clientHandler.sendMessage(PredefinedServerMessages.error("Client data not found on the server. Please ensure the client is registered and try again"));
            clientHandler.closeResources();
            return false;
        }
        return true;
    }


    /**
     * Asserts that the game is active.
     *
     * @see GameManager
     *
     * @param clientHandler the client handler
     * @return true if the game is active, false otherwise
     */
    public static boolean assertGameIsActive(@NonNull ClientHandler clientHandler) {
        if (!GameManager.getInstance().getIsGameActive().get()) {
            clientHandler.sendMessage(PredefinedServerMessages.error("You can only perform this action during an active game"));
            return false;
        }
        return true;
    }


    /**
     * Asserts that the game is not active.
     *
     * @see GameManager
     *
     * @param clientHandler the client handler
     * @return true if the game is not active, false otherwise
     */
    public static boolean assertGameIsNotActive(@NonNull ClientHandler clientHandler) {
        if (GameManager.getInstance().getIsGameActive().get()) {
            clientHandler.sendMessage(PredefinedServerMessages.error("You can't perform this action during an active game"));
            return false;
        }
        return true;
    }


    /**
     * Asserts that it is the {@link Player}'s turn.
     *
     * @see TurnManager
     *
     * @param clientHandler the client handler
     * @param clientId      the client ID
     * @return true if it is the player's turn, false otherwise
     */
    public static boolean assertPlayerTurn(@NonNull ClientHandler clientHandler, int clientId) {
        Player player = GameManager.getInstance().getPlayerByID(clientId);
        TurnManager turnManager = GameManager.getInstance().getTurnManager();

        if (turnManager.getCurrentPlayer() != player) {
            clientHandler.sendMessage(PredefinedServerMessages.error("Please wait till it is your turn"));
            return false;
        }
        return true;
    }


    /**
     * Asserts that the current phase matches the required phase.
     *
     * @see TurnManager
     *
     * @param clientHandler the client handler
     * @param requiredPhase the required game state
     * @return true if the current phase matches the required phase, false otherwise
     */
    public static boolean assertValidPhase(@NonNull ClientHandler clientHandler, @NonNull GameState requiredPhase) {
        if (GameManager.getInstance().getTurnManager().getCurrentPhase() != requiredPhase) {
            clientHandler.sendMessage(PredefinedServerMessages.error("You can only perform this action during the " + requiredPhase + " phase"));
            return false;
        }
        return true;
    }


    /**
     * Asserts that the provided {@link CardType} is valid.
     *
     * @param clientHandler the client handler
     * @param cardString    the card type as string
     * @return true if the card type is valid, false otherwise
     */
    public static boolean assertValidCardType(@NonNull ClientHandler clientHandler, @NonNull String cardString) {
        try {
            CardType.fromString(cardString);
        } catch (IllegalArgumentException e) {
            clientHandler.sendMessage(PredefinedServerMessages.error("You've provided an invalid card type."));
            return false;
        }
        return true;
    }
}
