package com.github.adrior.roborally.random_bot.handlers;

import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.handlers.IMessageHandler;
import com.github.adrior.roborally.message.utils.PredefinedClientMessages;
import com.github.adrior.roborally.random_bot.RandomBot;
import lombok.NonNull;

public class YourCardsHandler implements IMessageHandler<RandomBot> {

    @Override
    public void handle(@NonNull Message message, @NonNull RandomBot client) {
        String[] cardStrings = (String[]) message.messageBody().get("cardsInHand");
        int cardIndex = 0;

        // Skip the Again card for the first register.
        while(cardStrings[cardIndex].equals("Again")) {
            cardIndex++;
        }

        client.sleepMilliseconds(100);
        if (2 == client.getCurrentPhase().get()) {
            for (int i = 1; i < 6; i++) {
                client.sleepMilliseconds(30);
                client.sendMessage(PredefinedClientMessages.selectedCard(cardStrings[cardIndex++], i));
            }
        }
    }
}
