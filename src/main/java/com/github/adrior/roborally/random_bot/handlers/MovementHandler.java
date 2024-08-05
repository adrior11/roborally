package com.github.adrior.roborally.random_bot.handlers;

import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.handlers.IMessageHandler;
import com.github.adrior.roborally.random_bot.RandomBot;
import com.github.adrior.roborally.random_bot.RandomBotData;
import com.github.adrior.roborally.utility.Vector;
import lombok.NonNull;

public class MovementHandler implements IMessageHandler<RandomBot> {

    @Override
    public void handle(@NonNull Message message, @NonNull RandomBot client) {
        int clientId = (int) message.messageBody().get("clientID");
        int x = (int) message.messageBody().get("x");
        int y = (int) message.messageBody().get("y");

        RandomBotData clientData = client.findClientById(clientId);
        clientData.getRobot().setPosition(new Vector(x, y));
    }
}

