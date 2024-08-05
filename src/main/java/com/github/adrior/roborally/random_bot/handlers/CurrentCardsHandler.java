package com.github.adrior.roborally.random_bot.handlers;

import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.handlers.IMessageHandler;
import com.github.adrior.roborally.random_bot.RandomBot;
import lombok.NonNull;

import java.util.List;
import java.util.Map;

public class CurrentCardsHandler implements IMessageHandler<RandomBot> {

    @Override
    public void handle(@NonNull Message message, @NonNull RandomBot client) {
        List<Map<String, Object>> activeCards = (List<Map<String, Object>>) message.messageBody().get("activeCards");

        activeCards.forEach(player -> {
            int clientId = (int) player.get("clientID");
            String card = (String) player.get("card");

            if (client.getClientId() == clientId) client.setCurrentCard(card);
        });
    }
}

