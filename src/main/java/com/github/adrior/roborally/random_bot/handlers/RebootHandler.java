package com.github.adrior.roborally.random_bot.handlers;

import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.handlers.IMessageHandler;
import com.github.adrior.roborally.message.utils.PredefinedClientMessages;
import com.github.adrior.roborally.random_bot.RandomBot;
import com.github.adrior.roborally.utility.Orientation;
import lombok.NonNull;

import java.util.Locale;
import java.util.Random;

public class RebootHandler implements IMessageHandler<RandomBot> {

    private static final Random random = new Random();

    @Override
    public void handle(@NonNull Message message, @NonNull RandomBot client) {
        int clientId = (int) message.messageBody().get("clientID");

        Orientation[] orientations = Orientation.values();

        if (client.getClientId() == clientId) {
            client.sendMessage(PredefinedClientMessages.rebootDirection(orientations[random.nextInt(orientations.length)].toString().toLowerCase(Locale.ROOT)));
        }
    }
}

