package com.github.adrior.roborally.message.utils;

import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.Message.MessageType;
import lombok.experimental.UtilityClass;

/**
 * PredefinedClientMessages provides a collection of pre-created messages
 * that the client sends to the server.
 *
 * @see Message
 */
@UtilityClass
public class PredefinedClientMessages {

    /**
     * Creates a HelloServer message to send the group information, whether it's an AI,
     * and the protocol version from the client to the server.
     *
     * @param isAI Whether the client is an AI.
     * @param protocolVersion The version of the protocol.
     * @return A pre-defined HelloServer message.
     */
    public static Message helloServer(boolean isAI, String protocolVersion) {
        return new MessageBuilder()
                .setMessageType(MessageType.HELLO_SERVER)
                .addContent("isAI", isAI)
                .addContent("protocol", protocolVersion)
                .build();
    }


    /**
     * Creates a PLAYER_VALUES message to set the player's name and figure.
     *
     * @param name The player's name.
     * @param figure The player's figure.
     * @return A pre-defined PlayerValues message.
     */
    public static Message playerValues(String name, int figure) {
        return new MessageBuilder()
                .setMessageType(MessageType.PLAYER_VALUES)
                .addContent("name", name)
                .addContent("figure", figure)
                .build();
    }


    /**
     * Creates a SET_STATUS message to set the player's ready status.
     *
     * @param ready The player's ready status.
     * @return A pre-defined SetStatus message.
     */
    public static Message setStatus(boolean ready) {
        return new MessageBuilder()
                .setMessageType(MessageType.SET_STATUS)
                .addContent("ready", ready)
                .build();
    }


    /**
     * Creates a MAP_SELECTED message when a client selects a map.
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
     * Creates a SEND_CHAT message according to the protocol.
     *
     * @param message The chat message content.
     * @param to The clientId of the recipient. If -1, the message is broadcast to all clients.
     * @return A {@link Message} object representing the SEND_CHAT message.
     */
    public static Message sendChat(String message, int to) {
        return new MessageBuilder()
                .setMessageType(MessageType.SEND_CHAT)
                .addContent("message", message)
                .addContent("to", to)
                .build();
    }


    /**
     * Creates a PLAY_CARD message to play a game card.
     *
     * @param card The card to be played.
     * @return A pre-defined PlayCard message.
     */
    public static Message playCard(String card) {
        return new MessageBuilder()
                .setMessageType(MessageType.PLAY_CARD)
                .addContent("card", card)
                .build();
    }


    /**
     * Creates a SET_STARTING_POINT message to set the starting point.
     *
     * @param x The x coordinate of the starting point.
     * @param y The y coordinate of the starting point.
     * @return A pre-defined SetStartingPoint message.
     */
    public static Message setStartingPoint(int x, int y) {
        return new MessageBuilder()
                .setMessageType(MessageType.SET_STARTING_POINT)
                .addContent("x", x)
                .addContent("y", y)
                .build();
    }


    /**
     * Creates a SELECTED_CARD message to notify the server of a selected card.
     *
     * @param card The selected card.
     * @param register The register where the card is placed.
     * @return A pre-defined SelectedCard message.
     */
    public static Message selectedCard(String card, int register) {
        return new MessageBuilder()
                .setMessageType(MessageType.SELECTED_CARD)
                .addContent("card", card)
                .addContent("register", register)
                .build();
    }

    /**
     * Creates a SELECTED_DAMAGE message to notify the refilled shop cards.
     *
     * @param cards The list of selected damage cards.
     * @return A pre-defined SelectedDamage message.
     */
    public static Message selectedDamage(String[] cards) {
        return new MessageBuilder()
                .setMessageType(MessageType.SELECTED_DAMAGE)
                .addContent("cards", cards)
                .build();
    }


    /**
     * Creates a BUY_UPGRADE message to notify the server about buying a card.
     *
     * @param isBuying The flag if the player is buying the card.
     * @param card The selected card.
     * @return A pre-defined BuyUpgrade message.
     */
    public static Message buyUpgrade(boolean isBuying, String card) {
        return new MessageBuilder()
                .setMessageType(MessageType.BUY_UPGRADE)
                .addContent("isBuying", isBuying)
                .addContent("card", card)
                .build();
    }


    /**
     * Creates a REBOOT_DIRECTION message to notify the reboot direction.
     *
     * @param direction The direction of the reboot.
     * @return A pre-defined RebootDirection message.
     */
    public static Message rebootDirection(String direction) {
        return new MessageBuilder()
                .setMessageType(MessageType.REBOOT_DIRECTION)
                .addContent("direction", direction)
                .build();
    }


    /**
     * Creates a DISCARD_SOME message to notify the server of a selected card.
     *
     * @param cards The selected cards to discard.
     * @return A pre-defined DiscardSome message.
     */
    public static Message discardSome(String[] cards) {
        return new MessageBuilder()
                .setMessageType(MessageType.DISCARD_SOME)
                .addContent("cards", cards)
                .build();
    }


    /**
     * Creates a CHOOSE_REGISTER message to notify the server of the chosen register.
     *
     * @param register The chosen register.
     * @return A pre-defined ChooseRegister message.
     */
    public static Message chooseRegister(int register) {
        return new MessageBuilder()
                .setMessageType(MessageType.CHOOSE_REGISTER)
                .addContent("register", register)
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
