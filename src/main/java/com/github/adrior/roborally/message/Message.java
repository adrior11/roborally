package com.github.adrior.roborally.message;

import java.io.Serializable;
import java.util.Map;

/**
 * Represents a communication message, used in the game's client-server communication
 * protocol. This record encapsulates essential elements and structured content
 * defined by the communication protocol between client and server.
 *
 * @param messageType The type of protocol packet, dictating how it should be processed.
 * @param messageBody The structured content of the message, supporting complex data types.
 */
public record Message(MessageType messageType, Map<String, Object> messageBody) implements Serializable {

    /**
     * Enumerates the different types of protocol packets that can be used within the game,
     * facilitating identification and appropriate handling of various communications.
     * The enum conforms to Version 0.1 of the Hauptprojekt Protocol.
     */
    public enum MessageType {
        // Messages received by the client
        HELLO_CLIENT,           // Initial connection message from server=
        WELCOME,                // Confirmation of successful connection with client clientId
        PLAYER_ADDED,           // Confirmation of player addition
        PLAYER_STATUS,          // Broadcast player ready status
        SELECT_MAP,             // Server sends available maps
        GAME_STARTED,           // Server starts the game
        RECEIVED_CHAT,          // Receiving a chat message
        ERROR,                  // Error message
        CARD_PLAYED,            // Broadcast played card
        CURRENT_PLAYER,         // Notify current player
        ACTIVE_PHASE,           // Notify active game phase
        STARTING_POINT_TAKEN,   // Notify starting point taken
        YOUR_CARDS,             // Send cards to player
        NOT_YOUR_CARDS,         // Notify other players about card draw
        SHUFFLE_CODING,         // Notify card shuffle
        CARD_SELECTED,          // Notify card selection
        SELECTION_FINISHED,     // Notify selection finished
        TIMER_STARTED,          // Start timer
        TIMER_ENDED,            // End timer
        CARDS_YOU_GOT_NOW,      // Notify about drawn cards
        CURRENT_CARDS,          // Notify current cards
        REPLACE_CARD,           // Replace a card
        MOVEMENT,               // Notify movement
        PLAYER_TURNING,         // Notify player turning
        ANIMATION,              // Notify animation
        REBOOT,                 // Notify reboot
        REBOOT_DIRECTION,       // Notify the reboot direction
        ENERGY,                 // Notify energy change
        CHECK_POINT_REACHED,     // Notify checkpoint reached
        GAME_FINISHED,          // Notify game finished

        // Messages received by the server
        HELLO_SERVER,           // Initial connection message from a client
        PLAYER_VALUES,          // Setting player name and figure
        SET_STATUS,             // Setting player ready status
        MAP_SELECTED,           // Client selects a map
        SEND_CHAT,              // Sending a chat message
        PLAY_CARD,              // Play a game card
        SET_STARTING_POINT,     // Set starting point
        SELECTED_CARD,          // Notify selected card

        // Messages received by both client and server
        ALIVE,                  // Regular connection check

        // Protocol V1.0 Messages
        CONNECTION_UPDATE,      // Update client connection status
        DRAW_DAMAGE,            // Draw damage cards
        PICK_DAMAGE,            // Pick damage cards
        SELECTED_DAMAGE,        // Notify selected damage cards

        // Protocol V2.0 Messages
        REFILL_SHOP,            // Refill the upgrade shop
        EXCHANGE_SHOP,          // Exchange shop items
        BUY_UPGRADE,            // Buy an upgrade
        UPGRADE_BOUGHT,         // Confirm upgrade purchase
        DISCARD_SOME,           // Discard some cards
        CHECKPOINT_MOVED,       // Notify moved checkpoint
        CHOOSE_REGISTER,        // Choose a register
        REGISTER_CHOSEN,        // Register chosen
    }
}
