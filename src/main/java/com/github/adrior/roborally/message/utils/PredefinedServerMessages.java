package com.github.adrior.roborally.message.utils;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.github.adrior.roborally.core.tile.Tile;
import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.Message.MessageType;
import com.github.adrior.roborally.utility.GsonUtil;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PredefinedServerMessages provides a collection of pre-created messages
 * that the server sends to the client.
 *
 * @see Message
 */
@UtilityClass
public class PredefinedServerMessages {
    private static final String CLIENT_ID = "clientID";
    private static final String CARDS = "cards";
    private static final String REGISTER = "register";

    /**
     * Creates a HELLO_CLIENT message to send the protocol version from the server to the client.
     *
     * @param protocolVersion The version of the protocol.
     * @return A pre-defined HelloClient message.
     */
    public static Message helloClient(String protocolVersion) {
        return new MessageBuilder()
                .setMessageType(MessageType.HELLO_CLIENT)
                .addContent("protocol", protocolVersion)
                .build();
    }


    /**
     * Creates a WELCOME message to send the client clientId from the server to the client upon successful connection.
     *
     * @param clientID The client clientId assigned by the server.
     * @return A pre-defined Welcome message.
     */
    public static Message welcome(int clientID) {
        return new MessageBuilder()
                .setMessageType(MessageType.WELCOME)
                .addContent(CLIENT_ID, clientID)
                .build();
    }


    /**
     * Creates a PLAYER_ADDED message to confirm the addition of a player.
     *
     * @param clientID The client clientId of the added player.
     * @param name The player's name.
     * @param figure The player's figure.
     * @return A pre-defined PlayerAdded message.
     */
    public static Message playerAdded(int clientID, String name, int figure) {
        return new MessageBuilder()
                .setMessageType(MessageType.PLAYER_ADDED)
                .addContent(CLIENT_ID, clientID)
                .addContent("name", name)
                .addContent("figure", figure)
                .build();
    }


    /**
     * Creates a PLAYER_STATUS message to broadcast a player's ready status.
     *
     * @param clientID The client clientId of the player.
     * @param ready The ready status of the player.
     * @return A pre-defined PlayerStatus message.
     */
    public static Message playerStatus(int clientID, boolean ready) {
        return new MessageBuilder()
                .setMessageType(MessageType.PLAYER_STATUS)
                .addContent(CLIENT_ID, clientID)
                .addContent("ready", ready)
                .build();
    }


    /**
     * Creates a SELECT_MAP message to send available maps to the client.
     *
     * @param availableMaps The list of available maps.
     * @return A pre-defined SelectMap message.
     */
    public static Message selectMap(String[] availableMaps) {
        return new MessageBuilder()
                .setMessageType(MessageType.SELECT_MAP)
                .addContent("availableMaps", availableMaps)
                .build();
    }


    /**
     * Creates a MAP_SELECTED message to inform the clients of the selected map.
     *
     * @param map The name of the selected map.
     * @return A pre-defined MapSelected message.
     */
    public static Message mapSelected(String map) {
        return new MessageBuilder()
                .setMessageType(MessageType.MAP_SELECTED)
                .addContent("map", map)
                .build();
    }


    /**
     * Creates a GAME_STARTED message to notify the client that the game has started.
     *
     * @param gameMap The game map information.
     * @return A pre-defined GameStarted message.
     */
    public static Message gameStarted(List<List<List<Tile>>> gameMap) {
        JsonElement gameMapElement = GsonUtil.getGson().toJsonTree(gameMap, new TypeToken<List<List<List<Tile>>>>() {}.getType());
        return new MessageBuilder()
                .setMessageType(MessageType.GAME_STARTED)
                .addContent("gameMap", gameMapElement)
                .build();
    }


    /**
     * Creates a RECEIVED_CHAT message according to the protocol.
     *
     * @param message The chat message content.
     * @param from The clientId of the sender.
     * @param isPrivate Whether the message is private.
     * @return A {@link Message} object representing the RECEIVED_CHAT message.
     */
    public static Message receivedChat(String message, int from, boolean isPrivate) {
        return new MessageBuilder()
                .setMessageType(MessageType.RECEIVED_CHAT)
                .addContent("message", message)
                .addContent("from", from)
                .addContent("isPrivate", isPrivate)
                .build();
    }


    /**
     * Creates an ERROR message with a given error message.
     *
     * @param errorMessage The error message to be sent.
     * @return A pre-defined Error message.
     */
    public static Message error(String errorMessage) {
        return new MessageBuilder()
                .setMessageType(MessageType.ERROR)
                .addContent("error", errorMessage)
                .build();
    }


    /**
     * Creates an CONNECTION_UPDATE message with a given error message.
     *
     * @param clientID The clientId of the client who played the card.
     * @param isConnected Whether the client is connected.
     * @param action The action that has been taken.
     * @return A pre-defined ConnectionUpdate message.
     */
    public static Message connectionUpdate(int clientID, boolean isConnected, String action) {
        return new MessageBuilder()
                .setMessageType(MessageType.CONNECTION_UPDATE)
                .addContent(CLIENT_ID, clientID)
                .addContent("isConnected", isConnected)
                .addContent("action", action)
                .build();
    }


    /**
     * Creates a CARD_PLAYED message to broadcast a played card.
     *
     * @param clientID The clientId of the client who played the card.
     * @param card The card that was played.
     * @return A pre-defined CardPlayed message.
     */
    public static Message cardPlayed(int clientID, String card) {
        return new MessageBuilder()
                .setMessageType(MessageType.CARD_PLAYED)
                .addContent(CLIENT_ID, clientID)
                .addContent("card", card)
                .build();
    }


    /**
     * Creates a CURRENT_PLAYER message to notify the current player.
     *
     * @param clientID The clientId of the current player.
     * @return A pre-defined CurrentPlayer message.
     */
    public static Message currentPlayer(int clientID) {
        return new MessageBuilder()
                .setMessageType(MessageType.CURRENT_PLAYER)
                .addContent(CLIENT_ID, clientID)
                .build();
    }


    /**
     * Creates an ACTIVE_PHASE message to notify the active game phase.
     *
     * @param phase The active phase clientId.
     * @return A pre-defined ActivePhase message.
     */
    public static Message activePhase(int phase) {
        return new MessageBuilder()
                .setMessageType(MessageType.ACTIVE_PHASE)
                .addContent("phase", phase)
                .build();
    }


    /**
     * Creates a STARTING_POINT_TAKEN message to notify that a starting point has been taken.
     *
     * @param x The x coordinate of the starting point.
     * @param y The y coordinate of the starting point.
     * @param clientID The clientId of the client who took the starting point.
     * @return A pre-defined StartingPointTaken message.
     */
    public static Message startingPointTaken(int x, int y, int clientID) {
        return new MessageBuilder()
                .setMessageType(MessageType.STARTING_POINT_TAKEN)
                .addContent("x", x)
                .addContent("y", y)
                .addContent(CLIENT_ID, clientID)
                .build();
    }


    /**
     * Creates a REFILL_SHOP message to notify the refilled shop cards.
     *
     * @param cards The list of refilled cards.
     * @return A pre-defined YourCards message.
     */
    public static Message refillShop(String[] cards) {
        return new MessageBuilder()
                .setMessageType(MessageType.REFILL_SHOP)
                .addContent(CARDS, cards)
                .build();
    }


    /**
     * Creates a EXCHANGE_SHOP message to notify the exchanged shop cards.
     *
     * @param cards The list of exchanged cards.
     * @return A pre-defined YourCards message.
     */
    public static Message exchangeShop(String[] cards) {
        return new MessageBuilder()
                .setMessageType(MessageType.EXCHANGE_SHOP)
                .addContent(CARDS, cards)
                .build();
    }


    /**
     * Creates a UPGRADE_BOUGHT message to notify the bought upgrade card.
     *
     * @param clientID The clientId of the current player.
     * @param card The card that was bought.
     * @return A pre-defined YourCards message.
     */
    public static Message upgradeBought(int clientID, String card) {
        return new MessageBuilder()
                .setMessageType(MessageType.UPGRADE_BOUGHT)
                .addContent(CLIENT_ID, clientID)
                .addContent("card", card)
                .build();
    }


    /**
     * Creates a YOUR_CARDS message to send cards to a player.
     *
     * @param cardsInHand The list of cards in hand.
     * @return A pre-defined YourCards message.
     */
    public static Message yourCards(String[] cardsInHand) {
        return new MessageBuilder()
                .setMessageType(MessageType.YOUR_CARDS)
                .addContent("cardsInHand", cardsInHand)
                .build();
    }


    /**
     * Creates a NOT_YOUR_CARDS message to notify other players about a card draw.
     *
     * @param clientID The clientId of the client who drew cards.
     * @param cardsInHand The number of cards drawn.
     * @return A pre-defined NotYourCards message.
     */
    public static Message notYourCards(int clientID, int cardsInHand) {
        return new MessageBuilder()
                .setMessageType(MessageType.NOT_YOUR_CARDS)
                .addContent(CLIENT_ID, clientID)
                .addContent("cardsInHand", cardsInHand)
                .build();
    }


    /**
     * Creates a SHUFFLE_CODING message to notify about a card shuffle.
     *
     * @param clientID The clientId of the client who triggered the shuffle.
     * @return A pre-defined ShuffleCoding message.
     */
    public static Message shuffleCoding(int clientID) {
        return new MessageBuilder()
                .setMessageType(MessageType.SHUFFLE_CODING)
                .addContent(CLIENT_ID, clientID)
                .build();
    }


    /**
     * Creates a CARD_SELECTED message to notify card selection.
     *
     * @param clientID The clientId of the client who selected the card.
     * @param register The register where the card is placed.
     * @param filled Whether the register is filled.
     * @return A pre-defined CardSelected message.
     */
    public static Message cardSelected(int clientID, int register, boolean filled) {
        return new MessageBuilder()
                .setMessageType(MessageType.CARD_SELECTED)
                .addContent(CLIENT_ID, clientID)
                .addContent(REGISTER, register)
                .addContent("filled", filled)
                .build();
    }


    /**
     * Creates a SELECTION_FINISHED message to notify that the selection is finished.
     *
     * @param clientID The clientId of the client who finished selection.
     * @return A pre-defined SelectionFinished message.
     */
    public static Message selectionFinished(int clientID) {
        return new MessageBuilder()
                .setMessageType(MessageType.SELECTION_FINISHED)
                .addContent(CLIENT_ID, clientID)
                .build();
    }


    /**
     * Creates a TIMER_STARTED message to start the timer.
     *
     * @return A pre-defined TimerStarted message.
     */
    public static Message timerStarted() {
        return new MessageBuilder()
                .setMessageType(MessageType.TIMER_STARTED)
                .build();
    }


    /**
     * Creates a TIMER_ENDED message to end the timer.
     *
     * @param clientIDs The IDs of the clients affected by the timer ending.
     * @return A pre-defined TimerEnded message.
     */
    public static Message timerEnded(int[] clientIDs) {
        return new MessageBuilder()
                .setMessageType(MessageType.TIMER_ENDED)
                .addContent("clientIDs", clientIDs)
                .build();
    }


    /**
     * Creates a CARDS_YOU_GOT_NOW message to notify about drawn cards.
     *
     * @param cards The list of cards drawn.
     * @return A pre-defined CardsYouGotNow message.
     */
    public static Message cardsYouGotNow(String[] cards) {
        return new MessageBuilder()
                .setMessageType(MessageType.CARDS_YOU_GOT_NOW)
                .addContent(CARDS, cards)
                .build();
    }


    /**
     * Creates a CURRENT_CARDS message to notify current cards.
     *
     * @param activeCards The map of client clientId to card types.
     * @return A pre-defined CurrentCards message.
     */
    public static Message currentCards(@NonNull Map<Integer, String> activeCards) {
        List<Map<String, Object>> activeCardsList = activeCards.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> cardInfo = new HashMap<>();
                    cardInfo.put(CLIENT_ID, entry.getKey());
                    cardInfo.put("card", entry.getValue());
                    return cardInfo;
                })
                .toList();

        return new MessageBuilder()
                .setMessageType(MessageType.CURRENT_CARDS)
                .addContent("activeCards", activeCardsList)
                .build();
    }


    /**
     * Creates a REPLACE_CARD message to replace a card.
     *
     * @param register The register where the card is placed.
     * @param newCard The new card.
     * @param clientID The clientId of the client.
     * @return A pre-defined ReplaceCard message.
     */
    public static Message replaceCard(int register, String newCard, int clientID) {
        return new MessageBuilder()
                .setMessageType(MessageType.REPLACE_CARD)
                .addContent(REGISTER, register)
                .addContent("newCard", newCard)
                .addContent(CLIENT_ID, clientID)
                .build();
    }


    /**
     * Creates a MOVEMENT message to notify movement.
     *
     * @param clientID The clientId of the client.
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @return A pre-defined Movement message.
     */
    public static Message movement(int clientID, int x, int y) {
        return new MessageBuilder()
                .setMessageType(MessageType.MOVEMENT)
                .addContent(CLIENT_ID, clientID)
                .addContent("x", x)
                .addContent("y", y)
                .build();
    }


    /**
     * Creates a PLAYER_TURNING message to notify player turning.
     *
     * @param clientID The clientId of the client.
     * @param rotation The direction of the rotation.
     * @return A pre-defined PlayerTurning message.
     */
    public static Message playerTurning(int clientID, String rotation) {
        return new MessageBuilder()
                .setMessageType(MessageType.PLAYER_TURNING)
                .addContent(CLIENT_ID, clientID)
                .addContent("rotation", rotation)
                .build();
    }


    /**
     * Creates a DRAW_DAMAGE message to notify player drawing damage cards.
     *
     * @param clientID The clientId of the client.
     * @param cards The string array of cards.
     * @return A pre-defined DrawDamage message.
     */
    public static Message drawDamage(int clientID, String[] cards) {
        return new MessageBuilder()
                .setMessageType(MessageType.DRAW_DAMAGE)
                .addContent(CLIENT_ID, clientID)
                .addContent(CARDS, cards)
                .build();
    }


    /**
     * Creates a PICK_DAMAGE message to notify player to choose a damage card.
     *
     * @param count The count of cards to draw.
     * @param cards The string array of cards.
     * @return A pre-defined PickDamage message.
     */
    public static Message pickDamage(int count, String[] cards) {
        return new MessageBuilder()
                .setMessageType(MessageType.PICK_DAMAGE)
                .addContent("count", count)
                .addContent("availablePiles", cards)
                .build();
    }


    /**
     * Creates an ANIMATION message to notify animation.
     *
     * @param type The type of animation.
     * @return A pre-defined Animation message.
     */
    public static Message animation(String type) {
        return new MessageBuilder()
                .setMessageType(MessageType.ANIMATION)
                .addContent("type", type)
                .build();
    }


    /**
     * Creates a REBOOT message to notify reboot.
     *
     * @param clientID The clientId of the client.
     * @return A pre-defined Reboot message.
     */
    public static Message reboot(int clientID) {
        return new MessageBuilder()
                .setMessageType(MessageType.REBOOT)
                .addContent(CLIENT_ID, clientID)
                .build();
    }


    /**
     * Creates an ENERGY message to notify energy change.
     *
     * @param clientID The clientId of the client.
     * @param count The amount of energy change.
     * @param source The source of the energy change.
     * @return A pre-defined Energy message.
     */
    public static Message energy(int clientID, int count, String source) {
        return new MessageBuilder()
                .setMessageType(MessageType.ENERGY)
                .addContent(CLIENT_ID, clientID)
                .addContent("count", count)
                .addContent("source", source)
                .build();
    }


    /**
     * Creates a CHECK_POINT_REACHED message to notify checkpoint reached.
     *
     * @param clientID The clientId of the client.
     * @param number The number of the checkpoint reached.
     * @return A pre-defined CheckPointReached message.
     */
    public static Message checkpointReached(int clientID, int number) {
        return new MessageBuilder()
                .setMessageType(MessageType.CHECK_POINT_REACHED)
                .addContent(CLIENT_ID, clientID)
                .addContent("number", number)
                .build();
    }


    /**
     * Creates a GAME_FINISHED message to notify the game is finished.
     *
     * @param clientID The clientId of the client who won the game.
     * @return A pre-defined GameFinished message.
     */
    public static Message gameFinished(int clientID) {
        return new MessageBuilder()
                .setMessageType(MessageType.GAME_FINISHED)
                .addContent(CLIENT_ID, clientID)
                .build();
    }


    /**
     * Creates a CHECKPOINT_MOVED message to notify movement of a checkpoint.
     *
     * @param checkpointID The clientId of the checkpoint.
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @return A pre-defined CheckpointMoved message.
     */
    public static Message checkpointMoved(int checkpointID, int x, int y) {
        return new MessageBuilder()
                .setMessageType(MessageType.CHECKPOINT_MOVED)
                .addContent("checkpointID", checkpointID)
                .addContent("x", x)
                .addContent("y", y)
                .build();
    }


    /**
     * Creates a REGISTER_CHOSEN message to notify movement of a checkpoint.
     *
     * @param clientID The clientId of the client.
     * @param register The register which was chosen.
     * @return A pre-defined RegisterChosen message.
     */
    public static Message registerChosen(int clientID, int register) {
        return new MessageBuilder()
                .setMessageType(MessageType.REGISTER_CHOSEN)
                .addContent(CLIENT_ID, clientID)
                .addContent(REGISTER, register)
                .build();
    }


    /**
     * Creates an Alive message, used to check the connection status.
     *
     * @return A pre-defined Alive message.
     */
    public static Message alive() {
        return new MessageBuilder()
                .setMessageType(MessageType.ALIVE)
                .build();
    }
}
