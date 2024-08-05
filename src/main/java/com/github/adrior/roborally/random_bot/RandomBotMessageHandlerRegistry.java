package com.github.adrior.roborally.random_bot;

import com.github.adrior.roborally.message.Message.MessageType;
import com.github.adrior.roborally.message.handlers.MessageHandlerRegistry;
import com.github.adrior.roborally.random_bot.handlers.*;

/**
 * The RandomBotMessageHandlerRegistry class maintains a registry of message handlers
 * for different message types. It provides methods to register handlers and
 * retrieve handlers based on the message type.
 */
class RandomBotMessageHandlerRegistry extends MessageHandlerRegistry<RandomBot> {

    /**
     * Constructs a new TestClientMessageHandlerRegistry and registers default handlers
     * for various message types.
     */
    RandomBotMessageHandlerRegistry() {
        registerHandler(MessageType.HELLO_CLIENT,           new HelloClientHandler());
        registerHandler(MessageType.WELCOME,                new WelcomeHandler());
        registerHandler(MessageType.PLAYER_ADDED,           new PlayerAddedHandler());
        registerHandler(MessageType.PLAYER_STATUS,          new PlayerStatusHandler());
        registerHandler(MessageType.SELECT_MAP,             new SelectMapHandler());
        registerHandler(MessageType.MAP_SELECTED,           null);
        registerHandler(MessageType.GAME_STARTED,           new GameStartedHandler());
        registerHandler(MessageType.RECEIVED_CHAT,          null);
        registerHandler(MessageType.ERROR,                  new ErrorHandler());
        registerHandler(MessageType.CONNECTION_UPDATE,      new ConnectionUpdateHandler());
        registerHandler(MessageType.CARD_PLAYED,            null);
        registerHandler(MessageType.CURRENT_PLAYER,         new CurrentPlayerHandler());
        registerHandler(MessageType.ACTIVE_PHASE,           new ActivePhaseHandler());
        registerHandler(MessageType.STARTING_POINT_TAKEN,   new StartingPointTakenHandler());
        registerHandler(MessageType.REFILL_SHOP,            null);
        registerHandler(MessageType.EXCHANGE_SHOP,          null);
        registerHandler(MessageType.UPGRADE_BOUGHT,         null);
        registerHandler(MessageType.YOUR_CARDS,             new YourCardsHandler());
        registerHandler(MessageType.NOT_YOUR_CARDS,         null);
        registerHandler(MessageType.SHUFFLE_CODING,         null);
        registerHandler(MessageType.CARD_SELECTED,          null);
        registerHandler(MessageType.SELECTION_FINISHED,     null);
        registerHandler(MessageType.TIMER_STARTED,          null);
        registerHandler(MessageType.TIMER_ENDED,            null);
        registerHandler(MessageType.CARDS_YOU_GOT_NOW,      new CardsYouGotNowHandler());
        registerHandler(MessageType.CURRENT_CARDS,          new CurrentCardsHandler());
        registerHandler(MessageType.REPLACE_CARD,           null);
        registerHandler(MessageType.MOVEMENT,               new MovementHandler());
        registerHandler(MessageType.PLAYER_TURNING,         new PlayerTurningHandler());
        registerHandler(MessageType.DRAW_DAMAGE,            null);
        registerHandler(MessageType.PICK_DAMAGE,            new PickDamageHandler());
        registerHandler(MessageType.ANIMATION,              null);
        registerHandler(MessageType.REBOOT,                 new RebootHandler());
        registerHandler(MessageType.ENERGY,                 new EnergyHandler());
        registerHandler(MessageType.CHECK_POINT_REACHED,    null);
        registerHandler(MessageType.GAME_FINISHED,          new GameFinishedHandler());
        registerHandler(MessageType.CHECKPOINT_MOVED,       null);
        registerHandler(MessageType.REGISTER_CHOSEN,        null);
        registerHandler(MessageType.ALIVE,                  new AliveHandler());
    }
}
