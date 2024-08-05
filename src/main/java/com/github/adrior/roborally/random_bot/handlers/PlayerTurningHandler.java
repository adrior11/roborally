package com.github.adrior.roborally.random_bot.handlers;

import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.handlers.IMessageHandler;
import com.github.adrior.roborally.random_bot.RandomBot;
import com.github.adrior.roborally.random_bot.RandomBotData;
import com.github.adrior.roborally.utility.Orientation;
import lombok.NonNull;

public class PlayerTurningHandler implements IMessageHandler<RandomBot> {

    @Override
    public void handle(@NonNull Message message, @NonNull RandomBot client) {
        int clientId = (int) message.messageBody().get("clientID");
        String rotation = (String) message.messageBody().get("rotation");

        RandomBotData clientData = client.findClientById(clientId);
        Orientation orientation = clientData.getRobot().getOrientation();
        clientData.getRobot().setOrientation(
                rotation.equals("clockwise") ? orientation.turnRight() : orientation.turnLeft()
        );
    }
}

