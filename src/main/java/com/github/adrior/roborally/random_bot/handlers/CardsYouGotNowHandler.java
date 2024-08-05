package com.github.adrior.roborally.random_bot.handlers;

import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.handlers.IMessageHandler;
import com.github.adrior.roborally.random_bot.RandomBot;
import lombok.NonNull;

public class CardsYouGotNowHandler implements IMessageHandler<RandomBot> {

    @Override
    public void handle(@NonNull Message message, @NonNull RandomBot client) {
        String[] cards = (String[]) message.messageBody().get("cards");

        StringBuilder logMessage = new StringBuilder("Register have been filled with:\n");

        for (String card : cards) {
            logMessage.append(card).append("\n");
        }

        client.log(logMessage.toString());
    }
}

