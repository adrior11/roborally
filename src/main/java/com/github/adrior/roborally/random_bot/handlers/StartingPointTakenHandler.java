package com.github.adrior.roborally.random_bot.handlers;

import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.handlers.IMessageHandler;
import com.github.adrior.roborally.random_bot.RandomBot;
import com.github.adrior.roborally.random_bot.RandomBotData;
import com.github.adrior.roborally.utility.Orientation;
import com.github.adrior.roborally.utility.Vector;
import lombok.NonNull;

public class StartingPointTakenHandler implements IMessageHandler<RandomBot> {

    @Override
    public void handle(@NonNull Message message, @NonNull RandomBot client) {
        // Retrieve message content.
        int x = (int) message.messageBody().get("x");
        int y = (int) message.messageBody().get("y");
        int clientId = (int) message.messageBody().get("clientID");

        // Adjust the respective position of the client robot for the given clientId.
        RandomBotData clientData = client.findClientById(clientId);
        clientData.getRobot().setPosition(new Vector(x, y));
        clientData.getRobot().setOrientation(Orientation.RIGHT);

        client.log("Player " + clientId + " has taken starting point " + x + ":" + y);
    }
}

