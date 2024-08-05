package com.github.adrior.roborally.server.handlers;

import com.github.adrior.roborally.message.Message;
import com.github.adrior.roborally.message.handlers.IMessageHandler;
import com.github.adrior.roborally.server.ClientHandler;
import lombok.NonNull;

public class AliveHandler implements IMessageHandler<ClientHandler> {

    @Override
    public void handle(Message message, @NonNull ClientHandler clientHandler) {
        clientHandler.getReceivedAlive().set(true);
    }
}
