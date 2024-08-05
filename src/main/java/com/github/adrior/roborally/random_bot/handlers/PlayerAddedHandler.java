package com.github.adrior.roborally.random_bot.handlers;

import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.handlers.IMessageHandler;
import com.github.adrior.roborally.message.utils.PredefinedClientMessages;
import com.github.adrior.roborally.random_bot.RandomBot;
import com.github.adrior.roborally.random_bot.RandomBotData;
import com.github.adrior.roborally.random_bot.RandomBotData.Robot;
import lombok.NonNull;

public class PlayerAddedHandler implements IMessageHandler<RandomBot> {

    @Override
    public void handle(@NonNull Message message, @NonNull RandomBot client) {
        int clientId = (int) message.messageBody().get("clientID");
        String username = message.messageBody().get("name").toString();
        int figure = (int) message.messageBody().get("figure");

        // Set the received data for the respective client.
        RandomBotData clientData = new RandomBotData();

        clientData.setClientId(clientId);
        clientData.setUsername(username);
        clientData.setFigure(figure);
        clientData.setRobot(new Robot());

        client.getClients().add(clientData);

        if (client.getClientId() == clientId) {
            client.log("Connected");
            client.sendMessage(PredefinedClientMessages.setStatus(true));
        }
    }
}
