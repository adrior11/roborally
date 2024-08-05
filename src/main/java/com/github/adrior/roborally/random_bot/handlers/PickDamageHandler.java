package com.github.adrior.roborally.random_bot.handlers;

import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.handlers.IMessageHandler;
import com.github.adrior.roborally.message.utils.PredefinedClientMessages;
import com.github.adrior.roborally.random_bot.RandomBot;
import lombok.NonNull;

import java.util.Arrays;
import java.util.Random;

public class PickDamageHandler implements IMessageHandler<RandomBot> {

    private static final Random random = new Random();

    @Override
    public void handle(@NonNull Message message, @NonNull RandomBot client) {
       int count = (int) message.messageBody().get("count");
       String[] availablePiles = (String[]) message.messageBody().get("availablePiles");

       client.log(String.format("Selecting %d random cards from piles of %s", count, Arrays.toString(availablePiles)));

       String[] selectedCards = new String[count];

       for (int i = 0; i < count; i++) selectedCards[i] = availablePiles[random.nextInt(availablePiles.length)];

       client.log(String.format("Selected %s damage cards", Arrays.toString(selectedCards)));

       client.sendMessage(PredefinedClientMessages.selectedDamage(selectedCards));
    }
}
