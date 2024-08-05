package com.github.adrior.roborally.random_bot.handlers;

import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.handlers.IMessageHandler;
import com.github.adrior.roborally.message.utils.PredefinedClientMessages;
import com.github.adrior.roborally.random_bot.RandomBot;
import com.github.adrior.roborally.random_bot.RandomBotData;
import lombok.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class WelcomeHandler implements IMessageHandler<RandomBot> {

    private static final List<Integer> initialFigures = Arrays.asList(0,1,2,3,4,5);
    private static final Random random = new Random();

    @Override
    public void handle(@NonNull Message message, @NonNull RandomBot client) {
        int id = (int) message.messageBody().get("clientID");

        client.log("Received id: " + id);
        client.setClientId(id);

        int index = random.nextInt(getAvailableFigures(client).length);
        int randomFigure = getAvailableFigures(client)[index];

        client.sendMessage(PredefinedClientMessages.playerValues("Bot", randomFigure));
    }

    private int[] getAvailableFigures(@NonNull RandomBot client) {
        // Get the set of figures that are already taken
        List<Integer> takenFigures = client.getClients().stream()
                .map(RandomBotData::getFigure)
                .filter(figure -> -1 != figure)
                .toList();

        // Filter out taken figures from the initial list
        return initialFigures.stream()
                .filter(figure -> !takenFigures.contains(figure))
                .mapToInt(i -> i)
                .toArray();
    }
}
