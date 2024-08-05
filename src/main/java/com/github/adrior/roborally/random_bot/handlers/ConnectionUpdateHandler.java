package com.github.adrior.roborally.random_bot.handlers;

import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.handlers.IMessageHandler;
import com.github.adrior.roborally.random_bot.RandomBot;
import com.github.adrior.roborally.random_bot.RandomBotData;
import lombok.NonNull;

public class ConnectionUpdateHandler implements IMessageHandler<RandomBot> {

    @Override
    public void handle(@NonNull Message message, @NonNull RandomBot client) {
        int clientId = (int) message.messageBody().get("clientID");
        boolean isConnected = (boolean) message.messageBody().get("isConnected");
        String action = message.messageBody().get("action").toString();

        RandomBotData affectedClient = client.findClientById(clientId);

        if (!isConnected) {
            if ("Remove".equals(action) && null != affectedClient) client.getClients().remove(affectedClient);

            if (clientId == client.getClientId()) client.closeClient();
        }
    }
}
