package com.github.adrior.roborally.server;

import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.Message.MessageType;
import com.github.adrior.roborally.message.handlers.MessageHandlerRegistry;
import com.github.adrior.roborally.server.handlers.*;

/**
 * The ServerMessageHandlerRegistry class maintains a registry of {@link Message} handlers
 * for different message types. It provides methods to register handlers and
 * retrieve handlers based on the message type.
 *
 * @see MessageHandlerRegistry
 */
public class ServerMessageHandlerRegistry extends MessageHandlerRegistry<ClientHandler> {

    /**
     * Constructs a new ServerMessageHandlerRegistry and registers default handlers
     * for various message types.
     */
    ServerMessageHandlerRegistry() {
        registerHandler(MessageType.HELLO_SERVER,           new HelloServerHandler());
        registerHandler(MessageType.PLAYER_VALUES,          new PlayerValuesHandler());
        registerHandler(MessageType.SET_STATUS,             new SetStatusHandler());
        registerHandler(MessageType.MAP_SELECTED,           new MapSelectedHandler());
        registerHandler(MessageType.SEND_CHAT,              new SendChatHandler());
        registerHandler(MessageType.PLAY_CARD,              new PlayCardHandler());
        registerHandler(MessageType.SET_STARTING_POINT,     new SetStartingPointHandler());
        registerHandler(MessageType.BUY_UPGRADE,            new BuyUpgradeHandler());
        registerHandler(MessageType.SELECTED_CARD,          new SelectedCardHandler());
        registerHandler(MessageType.SELECTED_DAMAGE,        new SelectedDamageHandler());
        registerHandler(MessageType.REBOOT_DIRECTION,       new RebootDirectionHandler());
        registerHandler(MessageType.DISCARD_SOME,           new DiscardSomeHandler());
        registerHandler(MessageType.CHOOSE_REGISTER,        new ChooseRegisterHandler());
        registerHandler(MessageType.ALIVE,                  new AliveHandler());
    }
}
