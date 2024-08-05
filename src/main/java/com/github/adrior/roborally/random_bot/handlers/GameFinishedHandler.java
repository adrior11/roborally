package com.github.adrior.roborally.random_bot.handlers;

import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.handlers.IMessageHandler;
import com.github.adrior.roborally.random_bot.RandomBot;
import lombok.NonNull;

public class GameFinishedHandler implements IMessageHandler<RandomBot> {

    @Override
    public void handle(Message message, @NonNull RandomBot client) {
        client.log("Game finished!");
        client.closeResources();
    }
}
