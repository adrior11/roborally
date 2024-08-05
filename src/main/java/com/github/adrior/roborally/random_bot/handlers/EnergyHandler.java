package com.github.adrior.roborally.random_bot.handlers;

import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.handlers.IMessageHandler;
import com.github.adrior.roborally.random_bot.RandomBot;
import com.github.adrior.roborally.random_bot.RandomBotData;
import lombok.NonNull;

public class EnergyHandler implements IMessageHandler<RandomBot> {

    @Override
    public void handle(@NonNull Message message, @NonNull RandomBot client) {
        int clientId = (int) message.messageBody().get("clientID");
        int count = (int) message.messageBody().get("count");

        RandomBotData clientData = client.findClientById(clientId);
        clientData.getRobot().setEnergy(count);
    }
}

